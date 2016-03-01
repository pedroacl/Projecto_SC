package interfaces.dao;

import entities.ChatMessage;

public interface ChatMessageDAOInterface {
	public void addChatMessage(Long ConversationId, ChatMessage chatMessage);
	
}
