package service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dao.ConversationDAO;
import network.messages.ChatMessage;
import util.PersistenceUtil;

/**
 * 
 * @author António, José e Pedro
 *
 */
public class ConversationService {

	private static ConversationDAO conversationDAO;
	
	public ConversationService(String serverPassword) {
		conversationDAO = new ConversationDAO(serverPassword);
	}

	public void removeConversationFromUser(String username, String fromUser) {
		conversationDAO.removeConversationFromUser(username, fromUser);
	}

	public Long addChatMessage(ChatMessage chatMessage) {
		return conversationDAO.addChatMessage(chatMessage);
	}

	public void addConversationToUser(String username, String toUser, Long conversationId) {
		conversationDAO.addConversationToUser(username, toUser, conversationId);
	}

	public void removeConversation(Long conversationId) {
		conversationDAO.removeConversation(conversationId);

	}

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

	public ArrayList<Long> getAllConversationsFrom(String username) {
		return conversationDAO.getAllConversationsFrom(username);
	}

	public ChatMessage getLastChatMessage(String username, long conversationId) {
		return conversationDAO.getLastChatMessage(username, conversationId);
	}

	public Long getConversationInCommom(String username, String destination) {
		return conversationDAO.getConversationInCommom(username, destination);
	}

	public List<ChatMessage> getAllMessagesFromConversation(String username, Long conversationId) {
		return conversationDAO.getAllMessagesFromConversation(username,conversationId);
	}

	public String existsFile(String username, String destination, String fileName) {
		return conversationDAO.existFile(username, destination, fileName);
	}

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

	/**
	 * method to remove the key of a specific user in the conversations folder
	 * @param userToRemove name of the user that has been removed from the goup
	 * @param conversationId of the folder
	 */
	public void removeKeyUserFromFolder(String userToRemove, Long conversationId) {
		
		File f = new File("conversations/" + conversationId.toString() + "/keys");

		File[] listConversationKeyUser = f.listFiles();
		
		if(listConversationKeyUser != null) {
			for (File tempFile : listConversationKeyUser) {
				System.out.println("[ConversationService] removeKeyUserFromFolder: " + tempFile.getName());
				String[] temp = tempFile.getName().split("\\.");
				System.out.println("[ConversationService] removeKeyUserFromFolder: " + Arrays.toString(temp));
				if (userToRemove.equals(temp[temp.length - 1])) {
					System.out.println("[ConversationService] removeKeyUserFromFolder: " + temp[temp.length - 1]);
					tempFile.delete();
				}
			}
		}

	}

	public byte [] getChatMessageSignature(Long id, String name) {
		 return conversationDAO.getChatMessageSignature(id, name);
		
	}

	public byte[] getUserChatMessageKey(String username, Long id, String name) {
		return conversationDAO.getUserChatMessageKey(username, id, name);
	}

	public static String getSignatureProducer(Long id, String fileName) {
		return conversationDAO.getSignatureProducer(id,fileName);
	}

}
