package interfaces.dao;

import entities.ChatMessage;
import entities.Conversation;

public interface ConversationDAOInterface {
	
	public void addChatMessage(ChatMessage chatMessage);

	public Conversation getConversationByUsername(ChatMessage chatMessage);

	public Conversation getConversationById(Long conversationId);

	public ChatMessage getLastChatMessage(Long conversationId);

}
