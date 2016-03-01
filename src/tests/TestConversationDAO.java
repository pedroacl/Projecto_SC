package tests;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import persistence.ConversationDAO;

public class TestConversationDAO {
	
	private static ConversationDAO conversationDAO;

	@Before
    public void setUp() {
		conversationDAO = ConversationDAO.getInstance();
    }
	
    @After
    public void tearDown() {

    }
	
	@Test
	public void testAddChatMessage() {
		//conversationDAO.addChatMessage(chatMessage);
	}
}
