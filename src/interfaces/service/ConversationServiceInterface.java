package interfaces.service;

import java.util.ArrayList;

import entities.ChatMessage;

public interface ConversationServiceInterface {
	public void removeConversationsFromUser(String username);
	
	public Long addChatMessage(ChatMessage chatMessage);

	public void addConversationToUser(String userToAdd, String groupName, Long conversationId);

	public void removeConversation(Long conversationId);

	public String getFilePath(String fileName, Long conversationId);

	public ArrayList<Long> getAllConversationsFrom(String username);
	
	public ChatMessage getLastChatMessage(long id);

	public Long getConversationInCommom(String username, String destination);

	public ArrayList<ChatMessage> getAllMessagesFromConversation(Long conversationId);
	
	public String existFile(String username, String destination, String message);
}
