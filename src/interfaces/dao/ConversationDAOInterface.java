package interfaces.dao;

import java.util.List;

import entities.ChatMessage;
import entities.Conversation;

public interface ConversationDAOInterface {
	
	public Long addChatMessage(ChatMessage chatMessage);

	public Conversation getConversationById(Long conversationId);

	public ChatMessage getLastChatMessage(Long conversationId);
	
	public List<Long> getAllConversationsFrom(String userName);
	
	public String getFilePath(String fileName, Long conversationId);

}
