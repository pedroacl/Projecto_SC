package tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dao.ConversationDAO;
import dao.UserConversationHeaderDAO;
import domain.Authentication;
import entities.Conversation;
import factories.ChatMessageFactory;

public class TestUserConversationHeaderDAO {

	private static UserConversationHeaderDAO userConversationHeaderDAO;
	
	private static ConversationDAO conversationDAO;

	private static ChatMessageFactory chatMessageFactory;

	private static Authentication authentication;

	@Before
	public void setUp() {
		userConversationHeaderDAO = UserConversationHeaderDAO.getInstance();
		
		conversationDAO = ConversationDAO.getInstance();

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
	public void testAddUserConversationHeader() {
		Conversation conversation = conversationDAO.getConversationById(1L);
		userConversationHeaderDAO.addUserConversationHeader("pedro", conversation);
	}
}
