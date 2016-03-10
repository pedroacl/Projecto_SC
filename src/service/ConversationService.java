package service;

import java.io.File;
import java.util.ArrayList;

import dao.ConversationDAO;
import entities.ChatMessage;
import interfaces.service.ConversationServiceInterface;
import util.MiscUtil;

public class ConversationService implements ConversationServiceInterface {

	private static ConversationDAO conversationDAO;
	
	public ConversationService() {
		conversationDAO = ConversationDAO.getInstance();
	}

	@Override
	public void removeConversationsFromUser(String username) {
		conversationDAO.removeConversationsFromUser(username);
	}

	@Override
	public Long addChatMessage(ChatMessage chatMessage) {
		return conversationDAO.addChatMessage(chatMessage);
	}

	@Override
	public void addConversationToUser(String userToAdd, String groupName, Long conversationId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeConversation(Long conversationId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFilePath(String fileName, Long conversationId) {
		String filesDirectory = "conversations/" + conversationId + "/files";
		File file = new File(filesDirectory);

		if (file.exists())
			return "conversations/" + conversationId + "/files/" + fileName;
		else {
			MiscUtil.createFile("conversations/" + conversationId + "/files");
			return "conversations/" + conversationId + "/files/" + fileName;
		}
	}

	@Override
	public ArrayList<Long> getAllConversationsFrom(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChatMessage getLastChatMessage(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getConversationInCommom(String username, String destination) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ChatMessage> getAllMessagesFromConversation(Long conversationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String existFile(String username, String destination, String message) {
		// TODO Auto-generated method stub
		return null;
	}
}
