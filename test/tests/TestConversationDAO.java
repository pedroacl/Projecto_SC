package tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dao.ConversationDAO;
import dao.GroupDAO;
import domain.Authentication;
import entities.ChatMessage;
import factories.ConversationFactory;
import network.MessageType;

public class TestConversationDAO {

	private static ConversationDAO conversationDAO;

	private static GroupDAO groupDAO;

	private static ConversationFactory conversationFactory;

	private static Authentication authentication;

	@Before
	public void setUp() {
		try {
			FileUtils.deleteDirectory(new File("users"));
			FileUtils.deleteDirectory(new File("conversations"));
			FileUtils.deleteDirectory(new File("groups"));

			File file = new File("users.txt");

			if (file.exists()) {
				FileUtils.forceDelete(file);
			}
			
			file = new File("groups.txt");

			if (file.exists())
				FileUtils.forceDelete(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		conversationFactory = ConversationFactory.getInstance();
		conversationFactory.reset();

		conversationDAO = ConversationDAO.getInstance();
		groupDAO = GroupDAO.getInstance();
		authentication = Authentication.getInstance();

		authentication.addUser("antonio", "my_password");
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testAddChatMessage() {
		String fromUser = "maria";
		String toUser = "pedro";

		ChatMessage chatMessage = new ChatMessage(fromUser, toUser, "mensagem de teste 1", MessageType.MESSAGE);

		conversationDAO.addChatMessage(chatMessage);

		File file = new File("conversations/1/messages/" + chatMessage.getCreatedAt().getTime());
		assertThat(file.exists(), is(not(nullValue())));

		chatMessage = new ChatMessage(fromUser, toUser, "mensagem de teste 2", MessageType.MESSAGE);
		conversationDAO.addChatMessage(chatMessage);

		file = new File("conversations/1/messages/" + chatMessage.getCreatedAt().getTime());
		assertThat(file.exists(), is(true));
	}

	@Test
	public void testAddGroupMessage() {
		String groupName = "grupo1";
		String admin = "maria";
		String fromUser = "maria";
/*
		groupDAO.addUserToGroup(groupName, admin);
		File file = new File("groups/" + groupName);
		assertThat(file.exists(), is(true));
		
		ChatMessage chatMessage = new ChatMessage(fromUser, groupName, "mensagem de teste 1", MessageType.MESSAGE);
		conversationDAO.addChatMessage(chatMessage);

		file = new File("conversations/" + conversationId + "/messages/" + chatMessage.getCreatedAt().getTime());
		assertThat(file.exists(), is(true));
		*/
	}

	@Test
	public void testGetLastChatMessage() {
		String fromUser = "maria";
		String toUser = "pedro";
		Long conversationId = 1L;

		ChatMessage chatMessage1 = new ChatMessage(fromUser, toUser, "mensagem de teste 1", MessageType.MESSAGE);
		conversationDAO.addChatMessage(chatMessage1);

		ChatMessage chatMessage2 = new ChatMessage(fromUser, toUser, "mensagem de teste 2", MessageType.MESSAGE);
		conversationDAO.addChatMessage(chatMessage2);

		ChatMessage lastChatMessage = conversationDAO.getLastChatMessage(conversationId);

		assertThat(lastChatMessage, is(not(nullValue())));
		assertThat(lastChatMessage, is(chatMessage2));
	}

	@Test
	public void testGetAllConversationsFrom() {
		String fromUser = "maria";
		String toUser1 = "pedro";
		String toUser2 = "jose";

		ChatMessage chatMessage1 = new ChatMessage(fromUser, toUser1, "mensagem de teste 1", MessageType.MESSAGE);
		conversationDAO.addChatMessage(chatMessage1);

		ChatMessage chatMessage2 = new ChatMessage(fromUser, toUser2, "mensagem de teste 2", MessageType.MESSAGE);
		conversationDAO.addChatMessage(chatMessage2);

		List<Long> conversationsIds = conversationDAO.getAllConversationsFrom(fromUser);
		assertThat(conversationsIds, is(not(nullValue())));
		assertThat(conversationsIds, is(not(nullValue())));
		assertTrue(conversationsIds.size() > 1);
	}

	@Test
	public void testGetAllMessagesFromConversation() {
		String fromUser = "maria";
		String toUser = "pedro";
		Long conversationId = 1L;

		ChatMessage chatMessage1 = new ChatMessage(fromUser, toUser, "mensagem de teste 1", MessageType.MESSAGE);
		conversationDAO.addChatMessage(chatMessage1);

		List<ChatMessage> chatMessages = conversationDAO.getAllMessagesFromConversation(conversationId);
		assertThat(chatMessages, is(not(nullValue())));
		assertThat(chatMessages.size(), is(1));

		ChatMessage chatMessage2 = new ChatMessage(fromUser, toUser, "mensagem de teste 2", MessageType.MESSAGE);
		conversationDAO.addChatMessage(chatMessage2);

		chatMessages = conversationDAO.getAllMessagesFromConversation(conversationId);
		assertThat(chatMessages, is(not(nullValue())));
		assertThat(chatMessages.size(), is(2));
	}
}
