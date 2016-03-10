package dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import entities.ChatMessage;
import entities.Conversation;
import factories.ConversationFactory;
import interfaces.dao.ConversationDAOInterface;
import network.MessageType;
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

		// obtem Id de Conversação a partir do username
		Long conversationId = userConversations.get(chatMessage.getDestination());

		// nao existe conversa -> cria uma conversa entre os dois comunicantes
		if (conversationId == null) {
			conversation = conversationFactory.build(chatMessage.getFromUser(), chatMessage.getDestination());
			MiscUtil.createDir("conversations/" + conversation.getId());
			MiscUtil.createDir("conversations/" + conversation.getId() + "/messages");
			MiscUtil.createFile("conversations/" + conversation.getId() + "/conversation");

			// actualiza ficheiro conversaçoes acrescentado esta nova entrada
			addConversationToUser(chatMessage.getFromUser(), chatMessage.getDestination(), conversation.getId());
			addConversationToUser(chatMessage.getDestination(), chatMessage.getFromUser(), conversation.getId());

		} else {
			conversation = getConversationById(conversationId);
		}

		filePath = "conversations/" + conversation.getId() + "/conversation";

		// pode ser null
		Conversation auxConversation = (Conversation) MiscUtil.readObject(filePath);

		// caso ficheiro esteja vazio
		if (auxConversation == null) {
			conversation.setLastMessageDate(chatMessage.getCreatedAt());
			MiscUtil.writeObject(conversation, filePath);
		} else {
			auxConversation.setLastMessageDate(chatMessage.getCreatedAt());
			MiscUtil.writeObject(auxConversation, filePath);
		}

		// caso seja um mensagem com File
		if (chatMessage.getMessageType().equals(MessageType.FILE)) {
			// verifica se existe a pasta files na directoria da conversa
			File fileDirectory = new File("conversations/" + conversation.getId() + "/files");
			if (!fileDirectory.exists())
				MiscUtil.createDir("conversations/" + conversation.getId() + "/files");
		}

		// persiste mensagem
		String pathToTxt = "conversations/" + conversation.getId() + "/messages/"
				+ chatMessage.getCreatedAt().getTime();
		MiscUtil.createFile(pathToTxt);
		MiscUtil.writeStringToFile(chatMessage.getFromUser() + "\n" + chatMessage.getDestination() + "\n"
				+ chatMessage.getMessageType() + "\n" + chatMessage.getContent(), pathToTxt);

		return conversation.getId();
	}

	/**
	 * 
	 */
	@Override
	public void addConversationToUser(String username, String toUser, Long conversationId) {
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
	public ChatMessage getLastChatMessage(Long conversationId) {
		String path = "conversations/" + conversationId;
		Conversation lastConversation = (Conversation) MiscUtil.readObject(path + "/conversation");
		if (lastConversation == null)
			return null;
		else {
			long date = lastConversation.getLastMessageDate().getTime();
			ArrayList<String> texto = (ArrayList<String>) MiscUtil.readFromFile(path + "/messages/" + date);
			ChatMessage lastMessage = makeChatMessage(texto);
			lastMessage.setCreatedAt(lastConversation.getLastMessageDate());
			return lastMessage;
		}
	}

	private Conversation getConversationById(Long conversationId) {
		String path = "conversations/" + conversationId + "/conversation";
		File file = new File(path);
		Conversation conversation = null;

		if (!file.exists()) {
			return null;
		}

		conversation = (Conversation) MiscUtil.readObject(path);

		return conversation;
	}

	public static ConversationDAO getInstance() {
		return conversationDAO;
	}

	/**
	 * Devolve uma lista de Ids de conversaçoes que um dado user mantem
	 * 
	 * @param Username-
	 *            nome do utilizador de quem se pretende as conversas
	 * @return uma lista de ids das conversacoes ou null caso nao exista este
	 *         utilizador
	 */
	public ArrayList<Long> getAllConversationsFrom(String userName) {
		String path = "users/" + userName + "/conversations";
		// Debug
		System.out.println("[getAllConversationFrom]: " + path);
		HashMap<String, Long> conversations = (HashMap<String, Long>) MiscUtil.readObject(path);

		if (conversations == null)
			System.out.println("[getAllConversationFrom]: Conversation esta a null");

		Collection<Long> collection = conversations.values();
		return new ArrayList<Long>(collection);
	}

	public List<ChatMessage> getAllMessagesFromConversation(Long conversationId) {
		String path = "conversations/" + conversationId + "/messages";
		ArrayList<ChatMessage> allMessages = new ArrayList<ChatMessage>();
		File file = new File(path);
		String[] filesIn = file.list();

		for (int i = 0; i < filesIn.length; i++) {
			ArrayList<String> texto = (ArrayList<String>) MiscUtil.readFromFile(path + "/" + filesIn[i]);
			ChatMessage k = makeChatMessage(texto);

			k.setCreatedAt(new Date(Long.parseLong(filesIn[i])));
			allMessages.add(k);
		}

		return allMessages;

	}

	/**
	 * 
	 * @param texto
	 * @return
	 */
	private ChatMessage makeChatMessage(ArrayList<String> texto) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < texto.size(); i++) {
			if (i > 2)
				sb.append(texto.get(i));
		}
		ChatMessage message = new ChatMessage(texto.get(0), texto.get(1), sb.toString(),
				MessageType.valueOf(texto.get(2)));
		return message;
	}

	/**
	 * 
	 */
	public Long getConversationInCommom(String user1, String user2) {
		// vai as conversaçoes do user1
		String path = "users/" + user1 + "/conversations";
		HashMap<String, Long> conversations = (HashMap<String, Long>) MiscUtil.readObject(path);

		if (conversations == null)
			return (long) -1;

		// verifica se existe uma conversaçao com o user2
		Long id = conversations.get(user2);
		System.out.println("[getConversationInCommom]: id em comum = " + id);
		return id == null ? (long) -1 : id;
	}

	/**
	 * 
	 * @param fromUser
	 * @param toUser
	 * @param fileName
	 * @return
	 */
	public String existFile(String fromUser, String toUser, String fileName) {
		Long id = getConversationInCommom(fromUser, toUser);
		if (id == -1) {
			return null;
		} else {
			String path = "conversations/" + id + "/files/" + fileName;
			File f = new File(path);
			return f.exists() ? path : null;
		}
	}

	@Override
	public void removeConversationsFromUser(String user) {
		HashMap<String, Long> userConversations = null;
		String filePath = "users/" + user + "/conversations";
		File file = new File(filePath);

		if (file.exists() && file.length() != 0) {
			userConversations = (HashMap<String, Long>) MiscUtil.readObject(filePath);
			userConversations.remove(user);
			MiscUtil.writeObject(userConversations, filePath);
		}

	}

	@Override
	public void removeConversation(Long conversationId) {
		File file = new File("conversations/" + conversationId);
		MiscUtil.delete(file);

	}
}
