package tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dao.ConversationDAO;
import domain.Authentication;
import entities.ChatMessage;
import entities.Conversation;
import factories.ChatMessageFactory;
import factories.ConversationFactory;
import network.MessageType;

public class TestConversationDAO {

	private static ConversationDAO conversationDAO;

	private static ChatMessageFactory chatMessageFactory;

	private static ConversationFactory conversationFactory;

	private static Authentication authentication;

	@Before
	public void setUp() {
		conversationDAO = ConversationDAO.getInstance();
		conversationFactory = ConversationFactory.getInstance();
		chatMessageFactory = ChatMessageFactory.getInstance();
		authentication = Authentication.getInstance();

		try {
			FileUtils.deleteDirectory(new File("users"));
			FileUtils.deleteDirectory(new File("conversations"));

			File file = new File("users.txt");

			if (file.exists()) {
				FileUtils.forceDelete(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		authentication.addUser("antonio", "my_password");
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testAddChatMessage() {
		String fromUser = "maria";
		String toUser = "pedro";

		ChatMessage chatMessage = chatMessageFactory.build(fromUser, toUser, "mensagem de teste 1",
				MessageType.MESSAGE);

		conversationDAO.addChatMessage(chatMessage);

		File file = new File("conversations/1/" + chatMessage.getId());
		assertThat(file.exists(), is(not(nullValue())));

		chatMessage = chatMessageFactory.build(fromUser, toUser, "mensagem de teste 2", MessageType.MESSAGE);
		conversationDAO.addChatMessage(chatMessage);
		
		file = new File("conversations/1/" + chatMessage.getId());
		assertThat(file.exists(), is(not(nullValue())));

	}
	
	@Test
	public void getConversationById() {
		String fromUser = "maria";
		String toUser = "pedro";
		Long conversationId = 1L;

		ChatMessage chatMessage = chatMessageFactory.build(fromUser, toUser, "mensagem de teste 1",
				MessageType.MESSAGE);
		
		conversationDAO.addChatMessage(chatMessage);
		Conversation conversation = conversationDAO.getConversationById(conversationId);
	
		File file = new File("conversations/1/conversation");
		assertThat(file.exists(), is(not(nullValue())));
	
		assertThat(conversation, is(not(nullValue())));
	}
}
