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
import dao.UserConversationHeaderDAO;
import domain.Authentication;
import entities.Conversation;
import entities.UserConversationHeader;
import factories.ChatMessageFactory;
import factories.ConversationFactory;

public class TestUserConversationHeaderDAO {

	private static UserConversationHeaderDAO userConversationHeaderDAO;

	private static ConversationDAO conversationDAO;

	private static ChatMessageFactory chatMessageFactory;

	private static ConversationFactory conversationFactory;

	private static Authentication authentication;

	@Before
	public void setUp() {
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
		
		userConversationHeaderDAO = UserConversationHeaderDAO.getInstance();
		conversationFactory = ConversationFactory.getInstance();
		chatMessageFactory = ChatMessageFactory.getInstance();
		conversationDAO = ConversationDAO.getInstance();
		authentication = Authentication.getInstance();
		authentication.addUser("antonio", "my_password");
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testAddUsersConversationHeaders() {
		String fromUser = "maria";
		String toUser = "pedro";

		Conversation conversation = conversationFactory.build("maria", "pedro");

		userConversationHeaderDAO.addUsersConversationHeaders(conversation);
		Long conversationId = userConversationHeaderDAO.getConversationId(fromUser, toUser);
		assertThat(conversationId, is(not(nullValue())));
	}

	@Test
	public void testGetUserConversationHeader() {
		String fromUser = "maria";
		String toUser = "pedro";

		Conversation conversation = conversationFactory.build(fromUser, toUser);
		userConversationHeaderDAO.addUsersConversationHeaders(conversation);

		UserConversationHeader userConversationHeader = userConversationHeaderDAO.getUserConversationHeader(fromUser,
				toUser);
		
		assertThat(userConversationHeader, is(not(nullValue())));
		assertThat(userConversationHeader.getToUser(), is(toUser));
	}

	@Test
	public void testGetConversationId() {
		String fromUser = "maria";
		String toUser = "pedro";

		Conversation conversation = conversationFactory.build("maria", "pedro");

		userConversationHeaderDAO.addUsersConversationHeaders(conversation);
		Long conversationId = userConversationHeaderDAO.getConversationId(fromUser, toUser);

		assertThat(conversationId, is(not(nullValue())));;
		assertThat(conversationId, is(1L));
	}
}
