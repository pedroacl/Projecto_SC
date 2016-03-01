package interfaces.dao;

import java.util.List;

import entities.ChatMessage;
import entities.Conversation;

public interface ConversationDAOInterface {
	
	public void addChatMessage(ChatMessage chatMessage);

	public Conversation getConversationByUsername(ChatMessage chatMessage);

	public Conversation getConversationById(Long conversationId);

	public List<ChatMessage> getLastChatMessages(List<Long> conversationsIDs);

}
