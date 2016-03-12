package service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dao.ConversationDAO;
import entities.ChatMessage;
import interfaces.service.ConversationServiceInterface;
import util.MiscUtil;

public class ConversationService implements ConversationServiceInterface {

	private static ConversationDAO conversationDAO;
	
	public ConversationService() {
		conversationDAO = new ConversationDAO();
	}

	@Override
	public void removeConversationFromUser(String username, String fromUser) {
		conversationDAO.removeConversationFromUser(username, fromUser);
	}

	@Override
	public Long addChatMessage(ChatMessage chatMessage) {
		return conversationDAO.addChatMessage(chatMessage);
	}

	@Override
	public void addConversationToUser(String username, String toUser, Long conversationId) {
		conversationDAO.addConversationToUser(username, toUser, conversationId);
	}

	@Override
	public void removeConversation(Long conversationId) {
		conversationDAO.removeConversation(conversationId);
		
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
		return conversationDAO.getAllConversationsFrom(username);
	}

	@Override
	public ChatMessage getLastChatMessage(long conversationId) {
		return conversationDAO.getLastChatMessage(conversationId);
	}

	@Override
	public Long getConversationInCommom(String username, String destination) {
		return conversationDAO.getConversationInCommom(username, destination);
	}

	@Override
	public List<ChatMessage> getAllMessagesFromConversation(Long conversationId) {
		return conversationDAO.getAllMessagesFromConversation(conversationId);
	}

	@Override
	public String existsFile(String username, String destination, String fileName) {
		return conversationDAO.existFile(username, destination, fileName);
	}
}
