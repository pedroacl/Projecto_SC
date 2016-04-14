package interfaces.dao;

import java.io.File;
import java.util.List;

import network.messages.ChatMessage;

public interface ConversationDAOInterface {
	
	public Long addChatMessage(ChatMessage chatMessage);

	public ChatMessage getLastChatMessage(Long conversationId);
	
	public List<Long> getAllConversationsFrom(String userName);
	
	public List<ChatMessage> getAllMessagesFromConversation (Long conversationId);
	
	public Long getConversationInCommom(String user1, String user2);
	
	public void addConversationToUser(String username, String toUser, Long conversationId);

	public void removeConversation(Long conversationId);

	void removeConversationFromUser(String username, String fromUser);

	public File[] getConversationsFolders();
}
