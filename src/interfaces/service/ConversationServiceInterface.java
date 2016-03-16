package interfaces.service;

import java.util.List;

import entities.ChatMessage;

public interface ConversationServiceInterface {
	public void removeConversationFromUser(String username, String fromUser);
	
	public Long addChatMessage(ChatMessage chatMessage);

	public void addConversationToUser(String userToAdd, String groupName, Long conversationId);

	public void removeConversation(Long conversationId);

	public String getFilePath(String fileName, Long conversationId);

	public List<Long> getAllConversationsFrom(String username);
	
	public ChatMessage getLastChatMessage(long id);

	public Long getConversationInCommom(String username, String destination);

	public List<ChatMessage> getAllMessagesFromConversation(Long conversationId);
	
	public String existsFile(String username, String destination, String message);
	
	public Long getLastConversationId();
}
