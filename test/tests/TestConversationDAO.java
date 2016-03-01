package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dao.ConversationDAO;
import entities.ChatMessage;
import network.MessageType;

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
		ChatMessage chatMessage = new ChatMessage("maria", "pedro", "mensagem de teste", MessageType.MESSAGE);
		conversationDAO.addChatMessage(chatMessage);
	}
}
