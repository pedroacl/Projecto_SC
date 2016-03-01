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
import factories.ChatMessageFactory;
import network.MessageType;

public class TestConversationDAO {

	private static ConversationDAO conversationDAO;

	private static ChatMessageFactory chatMessageFactory;

	private static Authentication authentication;

	@Before
	public void setUp() {
		conversationDAO = ConversationDAO.getInstance();
		chatMessageFactory = ChatMessageFactory.getInstance();
		authentication = Authentication.getInstance();

		try {
			FileUtils.deleteDirectory(new File("users"));
			FileUtils.deleteDirectory(new File("conversations"));
			FileUtils.forceDelete(new File("users.txt"));
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
		ChatMessage chatMessage = chatMessageFactory.build("maria", "pedro", "mensagem de teste", MessageType.MESSAGE);
		conversationDAO.addChatMessage(chatMessage);

		File file = new File("conversations/" + chatMessage.getId());
		assertThat(file.exists(), is(not(nullValue())));
		
		//conversationDAO.getLastChatMessage(conversationsId);
	}
}
