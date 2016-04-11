package service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dao.ConversationDAO;
import entities.ChatMessage;
import interfaces.service.ConversationServiceInterface;
import util.PersistenceUtil;

/**
 * 
 * @author António, José e Pedro
 *
 */
public class ConversationService implements ConversationServiceInterface {

	private static ConversationDAO conversationDAO;

	private UserService userService;

	public ConversationService() {
		conversationDAO = new ConversationDAO();
		userService = new UserService();
	}

	@Override
	public void removeConversationFromUser(String username, String fromUser) {
		conversationDAO.removeConversationFromUser(username, fromUser);
	}

	@Override
	public Long addChatMessage(ChatMessage chatMessage) {
		// guardar chave privada associada ah mensagem
		String fileName = Long.toString(chatMessage.getCreatedAt().getTime());
		conversationDAO.saveUserFilePrivateKey(chatMessage.getFromUser(), fileName, chatMessage.getContent());
		conversationDAO.saveUserFilePrivateKey(chatMessage.getDestination(), fileName, chatMessage.getContent());

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

		if (file.exists()) {
			return addVersionToFile("conversations/" + conversationId + "/files/", fileName);
		} else {
			PersistenceUtil.createFile("conversations/" + conversationId + "/files");
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

	@Override
	public Long getLastConversationId() {
		File[] conversationsFolders = conversationDAO.getConversationsFolders();
		Long currentId = 0L;

		if (conversationsFolders == null)
			return currentId;

		Long maxId = currentId;

		for (File currentFile : conversationsFolders) {
			currentId = Long.parseLong(currentFile.getName());
			maxId = currentId > maxId ? currentId : maxId;
		}

		return maxId;
	}

	private String addVersionToFile(String path, String fileName) {

		String[] nameSplitted = fileName.split("\\.");

		String realFileName = nameSplitted[0];
		String extension = nameSplitted.length == 1 ? "" : "." + nameSplitted[1];

		File f = new File(path + fileName);
		int i = 1;

		while (f.exists()) {
			realFileName = nameSplitted[0] + i;
			i++;
			f = new File(path + realFileName + extension);
		}

		return path + realFileName + extension;
	}

}
