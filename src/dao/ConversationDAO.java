package dao;

import entities.ChatMessage;
import entities.Conversation;
import entities.UserConversationHeader;
import factories.ConversationFactory;
import interfaces.dao.ConversationDAOInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import util.MiscUtil;

public class ConversationDAO implements ConversationDAOInterface {

	private static ConversationDAO conversationDAO = new ConversationDAO();

	private static UserConversationHeaderDAO userConversationHeaderDAO;

	private ConversationFactory conversationFactory;

	private ConversationDAO() {
		conversationDAO = ConversationDAO.getInstance();
		userConversationHeaderDAO = UserConversationHeaderDAO.getInstance();
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

		Long conversationId = userConversationHeaderDAO.getConversationId(chatMessage.getFromUser(),
				chatMessage.getDestination());

		Conversation conversation = null;

		// conversa nao existe
		if (conversationId == null) {
			// criar conversa
			System.out.println("[ConversationDAO] Conversa nao existe");

			conversation = conversationFactory.build(chatMessage.getFromUser(), chatMessage.getDestination());
			
			conversationId = conversation.getId();

			MiscUtil.createFile("conversations/" + conversationId + "/conversation");
			
			MiscUtil.writeObject(conversation, "conversations/" + conversationId + "/conversation");
			
			userConversationHeaderDAO.addUsersConversationHeaders(conversation);
			

		} else {
			System.out.println("[ConversationDAO] Conversa jah existe");
			conversation = conversationDAO.getConversationById(conversationId);
		}

		// obter lista de chat messages
		try {
			// carregar ficheiro
			File file = new File("conversations/" + conversation.getId() + "/messages/message_" + chatMessage.getId());
			
			// ficheiro nao existe
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
				
				MiscUtil.writeObject(conversation, "conversations/" + 
						conversation.getId() + "/messages/message_" + chatMessage.getId());

				// ficheiro ja existe
			} else {
				System.out.println("Ficheiro: " + file.getAbsolutePath());
				System.out.println("Tamanho: " + file.length());

				if (file.length() == 0) {
					System.out.println("Ficheiro vazio!");
					conversation = conversationFactory.build(chatMessage.getFromUser(), chatMessage.getDestination());

				} else {
					FileInputStream fileInputStream = new FileInputStream(file);
					ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
					conversation = (Conversation) objectInputStream.readObject();

					if (conversation == null) {
						conversation = conversationFactory.build(chatMessage.getFromUser(),
								chatMessage.getDestination());
					}

					objectInputStream.close();
					fileInputStream.close();
				}
			}

			conversation.addChatMessage(chatMessage);

			// atualizar ficheiro
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(conversation);

			objectOutputStream.close();
			fileOutputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return conversationId;
	}

	/**
	 * 
	 * @param chatMessage
	 * @return
	 */
	@Override
	public Conversation getConversationByUsername(ChatMessage chatMessage) {

		File file = new File("users/" + chatMessage.getFromUser()
				+ "/conversations");

		if (!file.exists()) {
			return null;
		}

		List<UserConversationHeader> conversationHeaders = new ArrayList<UserConversationHeader>();

		// obter conversas do utilizador
		try {
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fin);
			conversationHeaders = (List<UserConversationHeader>) ois
					.readObject();

			ois.close();
			fin.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
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

		ChatMessage chatMessage = null;

		File file = new File("conversations/" + conversationId);

		if (!file.exists()) {
			return null;
		}

		File[] files = file.listFiles();
		int maxId = 0;

		// obter ultima mensagem
		for (File currentFile : files) {
			String fileName = currentFile.getName();
			int messageId = Integer.parseInt(fileName.split("_")[1]);

			if (messageId > maxId)
				maxId = messageId;
		}

		file = new File("conversations/" + conversationId
				+ "/messages/message_" + maxId);

		if (!file.exists()) {
			return null;
		}

		// ler ficheiro
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					fileInputStream);
			chatMessage = (ChatMessage) objectInputStream.readObject();

			objectInputStream.close();
			fileInputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return chatMessage;
	}

	@Override
	public Conversation getConversationById(Long conversationId) {
		File file = new File("conversations/" + conversationId
				+ "/conversation");
		Conversation conversation = null;

		if (!file.exists()) {
			return null;
		}

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					fileInputStream);

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
		File file = new File (filesDirectory);
		if(file.exists())
			return "conversations/" + conversationId + "/files/fileName";
		else {
			MiscUtil.createFile("conversations/" + conversationId + "/files");
			return "conversations/" + conversationId + "/files/fileName";
		}
	}
}
