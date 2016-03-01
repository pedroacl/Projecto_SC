package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import entities.ChatMessage;
import entities.Conversation;
import entities.ConversationHeader;
import factories.ConversationFactory;
import interfaces.dao.ConversationDAOInterface;

public class ConversationDAO implements ConversationDAOInterface {
	
	private static ConversationDAO conversationDAO = new ConversationDAO();
	
	private static UserConversationHeaderDAO conversationHeaderDAO;
	
	
	private ConversationDAO() {
		conversationDAO = ConversationDAO.getInstance();
		conversationHeaderDAO = UserConversationHeaderDAO.getInstance();
	}
	
	
	/**
	 * Função que permite persistir uma determinada mensagem em disco
	 * @param chatMessage Mensagem a ser guardada
	 */
	@Override
	public void addChatMessage(ChatMessage chatMessage) {

		ConversationFactory conversationFactory = ConversationFactory.getInstance();
		
		ConversationHeader conversationHeader = 
				conversationHeaderDAO.getUserConversationHeader(chatMessage.getFromUser(), chatMessage.getDestination());
		
		Conversation conversation = null;
		
		File file;
		
		//criar conversa
		if (conversationHeader == null) {
			conversation = conversationFactory.build(chatMessage.getFromUser(), chatMessage.getDestination());
		} else {
			conversation = conversationDAO.getConversationById(conversationHeader.getConversationId());
		}

		
		//obter lista de chat messages
		try {
			//carregar ficheiro
			System.out.println("NULL??");
			System.out.println(conversation == null);
			file = new File("conversations/" + conversation.getId() + "/message_" + chatMessage.getId());
		
			//ficheiro nao existe
			if (!file.exists()) {
				System.out.println("Criar mensagem");
				
				file.getParentFile().mkdirs();
				file.createNewFile();

				FileOutputStream fileOutputStream = new FileOutputStream(file);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

				objectOutputStream.writeObject(conversation);

			//ficheiro ja existe
			} else {
				System.out.println("Ficheiro: " + file.getAbsolutePath());
				System.out.println("Tamanho: " + file.length());
				
				if (file.length() == 0) {
					System.out.println("Ficheiro vazio!");
					conversation = conversationFactory.build(
							chatMessage.getFromUser(), chatMessage.getDestination());
					
				} else {
					FileInputStream fileInputStream = new FileInputStream(file);
					ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
					conversation = (Conversation) objectInputStream.readObject();
					
					if (conversation == null) {
						conversation = conversationFactory.build(
								chatMessage.getFromUser(), chatMessage.getDestination());
					}
				
					objectInputStream.close();
					fileInputStream.close();
				}
			}
		
			conversation.addChatMessage(chatMessage);
		
			//atualizar ficheiro
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(conversation);

			objectOutputStream.close();
			fileOutputStream.close();
			
			//atualizar conversation headers de cada user
			conversationHeaderDAO.addUserConversationHeader(conversation.getFromUser(), conversation);
			conversationHeaderDAO.addUserConversationHeader(conversation.getToUser(), conversation);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param chatMessage
	 * @return
	 */
	@Override
	public Conversation getConversationByUsername(ChatMessage chatMessage) {

		File file = new File("users/" + chatMessage.getFromUser() + "/conversations");
		
		if (!file.exists()) {
			return null;
		}
		
		List<ConversationHeader> conversationHeaders = new ArrayList<ConversationHeader>();
		
		//obter conversas do utilizador
		try {
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fin);
			conversationHeaders = (List<ConversationHeader>) ois.readObject();
			
			fin.close();
			ois.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//obter conversa
		for (ConversationHeader conversationHeader : conversationHeaders) {
			if (conversationHeader.getToUser().equals(chatMessage.getDestination())) {
				return getConversationById(conversationHeader.getConversationId());
			}
		}
	
		return null;
	}
	

	/**
	 * Função que devolve uma conversa 
	 * @param conversationId ID da conversa
	 * @return Devolve uma conversa caso esta exista ou null caso contrário
	 */
	@Override
	public Conversation getConversationById(Long conversationId) {
		File file = new File("conversations/" + conversationId + "/conversation");

		if (!file.exists())
			return null;
		
		Conversation conversation = null;
		
		try {
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fin);
			conversation = (Conversation) ois.readObject();

			ois.close();
			fin.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return conversation;
	}
	

	/**
	 * Função que obtem uma lista das ultimas mensagens de cada conversa do utilizador
	 * @param conversationsIDs Lista de IDs de cada conversa em que o utilizador participou
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
	
		//obter ultima mensagem
		for (File currentFile : files) {
			String fileName = currentFile.getName();
			int messageId = Integer.parseInt(fileName.split("_")[1]);
			
			if (messageId > maxId)
				maxId = messageId;
		}
		
		//ler ficheiro
		try {
			file = new File("conversations/" + conversationId + "/message_" + maxId);
			
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
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
	

	public static ConversationDAO getInstance() {
		return conversationDAO;
	}

	
	public String getFilePath(String username, String destination, String message) {		
		return null;
	}
}
