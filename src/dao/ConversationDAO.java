package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import network.MessageType;
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

		String filePath = "users/" + chatMessage.getFromUser() + "/conversations";
		File file = new File(filePath);

		if (!file.exists() || file.length() == 0) {
			userConversations = new HashMap<String, Long>();
		} else {
			userConversations = (HashMap<String, Long>) MiscUtil.readObject(filePath);
		}
		
		//obtem Id de Conversação a partir do do username
		Long conversationId = userConversations.get(chatMessage.getFromUser());

		// nao existe conversa -> criar uma conversa entre os dois comunicantes
		if (conversationId == null) {
			conversation = conversationFactory.build(chatMessage.getFromUser(), chatMessage.getDestination());
			MiscUtil.createDir("conversations/" + conversation.getId());
			MiscUtil.createDir("conversations/" + conversation.getId() + "/messages");
			MiscUtil.createFile("conversations/" + conversation.getId() + "conversation");
			
		} else {
			conversation = getConversationById(conversationId);
		}
		
		//actualiza ficheiro conversaçoes acrescentado esta nova entrada
		updateUserConversations(chatMessage.getFromUser(), chatMessage.getDestination(), conversation.getId());
		updateUserConversations(chatMessage.getDestination(), chatMessage.getFromUser(), conversation.getId());

		filePath = "conversations/" + conversation.getId() + "/conversation";
		file = new File(filePath);
		
		//pode ser null
		Conversation auxConversation = (Conversation) MiscUtil.readObject(filePath);
		
		
		//caso ficheiro esteja vazio
		if(auxConversation == null) {
			conversation.setLastMessageDate(chatMessage.getCreatedAt());
			MiscUtil.writeObject(conversation, filePath);
		}
		else {
			auxConversation.setLastMessageDate(chatMessage.getCreatedAt());
			MiscUtil.writeObject(auxConversation, filePath);
		}
		
		//persiste mensagem
		String pathToTxt = "conversations/" + conversation.getId() + 
				"/messages/" + chatMessage.getCreatedAt() + ".txt";
		MiscUtil.createFile(pathToTxt);
		MiscUtil.writeStringToFile(chatMessage.getFromUser() + "\n" + chatMessage.getDestination()
				+ "\n" + chatMessage.getMessageType() + "\n"+ chatMessage.getContent(), pathToTxt);

		return conversationId;
	}

	private void updateUserConversations(String username, String toUser, Long conversationId) {
		HashMap<String, Long> userConversations = null;
		String filePath = "users/" + username + "/conversations";
		File file = new File(filePath);

		if (file.exists() && file.length() != 0) {
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
	public ChatMessage getLastChatMessage(long conversationId) {
		String path = "/conversations/" + conversationId;
		Conversation lastConversation = (Conversation) MiscUtil.readObject(path + "/conversation");
		if(lastConversation == null)
			return null;
		else {
			String date = lastConversation.getLastMessageDate().toString();
			ArrayList<String> texto =(ArrayList<String>) MiscUtil.readFromFile(path + "/date");
			ChatMessage lastMessage = makeChatMessage(texto);
			lastMessage.setCreatedAt(lastConversation.getLastMessageDate());
			return lastMessage;
		}
	}

	@Override
	public Conversation getConversationById(long conversationId) {
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

	public String getFilePath(String fileName, long conversationId) {
		String filesDirectory = "conversations/" + conversationId + "/files";
		File file = new File(filesDirectory);
		if (file.exists())
			return "conversations/" + conversationId + "/files/fileName";
		else {
			MiscUtil.createFile("conversations/" + conversationId + "/files");
			return "conversations/" + conversationId + "/files/fileName";
		}
	}
	
	/**
	 * Devolve uma lista de Ids de conversaçoes que um dado user mantem
	 * @param Username- nome do utilizador de quem se pretende as conversas
	 * @return uma lista de ids das conversacoes ou null caso nao exista este utilizador
	 */
	public List<Long> getAllConversationsFrom(String userName) {
		String path ="/users/" + userName + "/conversations"; 
		HashMap<String,Long> conversations = (HashMap<String,Long>) MiscUtil.readObject(path);
		Collection<Long> collection = conversations.values();
		return new ArrayList<Long> (collection);
	}
	
	public List<ChatMessage> getAllMessagesFromConversation (long conversationId) {
		String path = "/conversations/" + conversationId + "/messages";
		ArrayList<ChatMessage> allMessages = new ArrayList<ChatMessage>(); 
		File file = new File(path);
		String[] filesIn = file.list();
		for(int i = 0; i < filesIn.length; i++) {
			ArrayList<String> texto = (ArrayList<String>) MiscUtil.readFromFile(filesIn[i]);
			ChatMessage k = makeChatMessage(texto);
			k.setCreatedAt(null);//como converter string num Date??????????
			allMessages.add(k);
		}
		
		return allMessages;
		
		
	}

	private ChatMessage makeChatMessage(ArrayList<String> texto) {
		String from= null;
		String to = null;
		String type = null;
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < texto.size(); i++) {
			if(i == 0) 
				from = texto.get(i);
			if(i == 1)
				to = texto.get(i);
			if(i == 2)
				type = texto.get(i);
			else
				sb.append(texto.get(i));	
		}
		ChatMessage message = new ChatMessage(from, to, sb.toString(), MessageType.valueOf(type));
		return message;
	}
	
}
