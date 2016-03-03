package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.HashMap;

import entities.ChatMessage;
import entities.Conversation;
import factories.ConversationFactory;
import interfaces.dao.ConversationDAOInterface;
import util.MiscUtil;

public class ConversationDAO implements ConversationDAOInterface {

	private static ConversationDAO conversationDAO = new ConversationDAO();

	private ConversationFactory conversationFactory;

	private ConversationDAO() {
		conversationDAO = ConversationDAO.getInstance();
		conversationFactory = ConversationFactory.getInstance();
	}

	/**
	 * Função que permite persistir uma determinada mensagem em disco
	 * 
	 * @param chatMessage
	 *            Mensagem a ser guardada
	 */
	@Override
	public Long addChatMessage(ChatMessage chatMessage) {

		Conversation conversation = null;
		HashMap<String, Long> userConversations = null;

		String filePath = "users/" + chatMessage.getFromUser();
		File file = new File(filePath);

		if (!file.exists()) {
			userConversations = new HashMap<String, Long>();
		} else {
			userConversations = (HashMap<String, Long>) MiscUtil.readObject(filePath);
		}

		Long conversationId = userConversations.get(chatMessage.getFromUser());

		// nao existe conversa
		if (conversationId == null) {
			conversation = conversationFactory.build(chatMessage.getFromUser(), chatMessage.getDestination());
		} else {
			conversation = getConversationById(conversationId);
		}

		MiscUtil.createDir("conversations/" + conversation.getId());
		MiscUtil.createDir("conversations/" + conversation.getId() + "/messages");
		MiscUtil.createFile("conversations/" + conversation.getId() + "conversation");

		updateUserConversations(chatMessage.getFromUser(), chatMessage.getDestination(), conversation.getId());
		updateUserConversations(chatMessage.getDestination(), chatMessage.getFromUser(), conversation.getId());

		filePath = "conversations/" + conversation.getId() + "/conversation";
		file = new File(filePath);

		Conversation auxConversation = (Conversation) MiscUtil.readObject(filePath);
		auxConversation.setLastMessageDate(new Date());

		return conversationId;
	}

	private void updateUserConversations(String username, String toUser, Long conversationId) {
		HashMap<String, Long> userConversations = null;
		String filePath = "users/" + username + "/conversations";
		File file = new File(filePath);

		if (file.exists()) {
			userConversations = (HashMap<String, Long>) MiscUtil.readObject(filePath);

		} else {
			userConversations = new HashMap<String, Long>();
			MiscUtil.createFile(filePath);
		}

		userConversations.put(toUser, conversationId);
		MiscUtil.writeObject(userConversations, filePath);
	}

	/**
	 * Função que obtem uma lista das ultimas mensagens de cada conversa do
	 * utilizador
	 * 
	 * @param conversationsIDs
	 *            Lista de IDs de cada conversa em que o utilizador participou
	 * @return Lista das ultimas mensagens
	 */
	@Override
	public ChatMessage getLastChatMessage(Long conversationId) {
		// TODO
		return null;
	}

	@Override
	public Conversation getConversationById(Long conversationId) {
		File file = new File("conversations/" + conversationId + "/conversation");
		Conversation conversation = null;

		if (!file.exists()) {
			return null;
		}

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

			conversation = (Conversation) objectInputStream.readObject();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return conversation;
	}

	public static ConversationDAO getInstance() {
		return conversationDAO;
	}

	public String getFilePath(String fileName, Long conversationId) {
		String filesDirectory = "conversations/" + conversationId + "/files";
		File file = new File(filesDirectory);
		if (file.exists())
			return "conversations/" + conversationId + "/files/fileName";
		else {
			MiscUtil.createFile("conversations/" + conversationId + "/files");
			return "conversations/" + conversationId + "/files/fileName";
		}
	}
}
