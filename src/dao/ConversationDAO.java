package dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import entities.Conversation;
import factories.ConversationFactory;
import network.messages.ChatMessage;
import network.messages.MessageType;
import util.PersistenceUtil;

/**
 * Classe que persiste conversações, isto é, gere as pastas das conversações e
 * guarda as mensagens de enviadas
 * 
 * @author António, José, Pedro
 */
public class ConversationDAO {

	private ConversationFactory conversationFactory;

	public ConversationDAO() {
		conversationFactory = ConversationFactory.getInstance();
	}

	/**
	 * Função que permite persistir uma determinada mensagem em disco
	 * 
	 * @param chatMessage
	 *            Mensagem a ser guardada
	 * @return Long com identificador da conversa onde esta mensagem está
	 *         guardada
	 * @requires chatMessage != null;
	 */
	public Long addChatMessage(ChatMessage chatMessage) {

		Conversation conversation = null;
		HashMap<String, Long> userConversations = null;

		// localizacao do registo de conversas
		String filePath = "users/" + chatMessage.getFromUser() + "/conversations";
		File file = new File(filePath);

		// registo de conversas nao existe ou encontra se vazio
		if (!file.exists() || file.length() == 0) {
			userConversations = new HashMap<String, Long>();
		} else {
			userConversations = (HashMap<String, Long>) PersistenceUtil.readObject(filePath);
		}

		// obtem Id da conversação a partir do username do destinatario
		Long conversationId = userConversations.get(chatMessage.getDestination());

		// nao existe conversa -> cria uma conversa entre os dois comunicantes
		if (conversationId == null) {

			// cria conversa e respectivas pastas
			conversation = conversationFactory.build(chatMessage.getFromUser(), chatMessage.getDestination());
			PersistenceUtil.createDir("conversations/" + conversation.getId());
			PersistenceUtil.createDir("conversations/" + conversation.getId() + "/messages");
			PersistenceUtil.createFile("conversations/" + conversation.getId() + "/conversation");
			conversationId = conversation.getId();

			// actualiza ficheiro conversaçoes de cada user acrescentando esta
			// nova entrada
			addConversationToUser(chatMessage.getFromUser(), chatMessage.getDestination(), conversation.getId());
			addConversationToUser(chatMessage.getDestination(), chatMessage.getFromUser(), conversation.getId());

		} else {
			conversation = getConversationById(conversationId);
		}

		filePath = "conversations/" + conversationId + "/conversation";

		// pode ser null
		Conversation auxConversation = (Conversation) PersistenceUtil.readObject(filePath);

		// caso ficheiro esteja vazio
		if (auxConversation == null) {
			conversation = new Conversation(chatMessage.getFromUser(), chatMessage.getDestination());
			conversation.setId(conversationId);
			conversation.setLastMessageDate(chatMessage.getCreatedAt());
			PersistenceUtil.writeObject(conversation, filePath);
		} else {
			// atualizar ultima mensagem da conversa
			auxConversation.setLastMessageDate(chatMessage.getCreatedAt());
			PersistenceUtil.writeObject(auxConversation, filePath);
		}

		// caso seja uma mensagem com um ficheiro
		if (chatMessage.getMessageType().equals(MessageType.FILE)) {
			// verifica se existe a pasta files na directoria da conversa
			File fileDirectory = new File("conversations/" + conversation.getId() + "/files");
			if (!fileDirectory.exists())
				PersistenceUtil.createDir("conversations/" + conversation.getId() + "/files");
		}

		// persiste mensagem
		String pathToTxt = "conversations/" + conversation.getId() + "/messages/"
				+ chatMessage.getCreatedAt().getTime();

		PersistenceUtil.createFile(pathToTxt);
		PersistenceUtil.writeStringToFile(chatMessage.getFromUser() + "\n" + chatMessage.getDestination() + "\n"
				+ chatMessage.getMessageType() + "\n" + chatMessage.getContent(), pathToTxt);

		return conversation.getId();
	}

	/**
	 * Adiciona uma conversaçao ao ficheiros de conversaçoes do user
	 * 
	 * @param username
	 *            Utilizador cuja conversaçao vai ser actulaizada
	 * @param toUser
	 *            Nome do utilizador com o qual se esta a conversar
	 * @param conversationId
	 *            Identificador da conversação entre username e toUser
	 * @requires username != null && toUser != null && conversationId != null
	 * 
	 */
	public void addConversationToUser(String username, String toUser, Long conversationId) {
		HashMap<String, Long> userConversations = null;
		String filePath = "users/" + username + "/conversations";
		File file = new File(filePath);

		// ficheiro jah existe e nao estah vazio
		if (file.exists() && file.length() != 0) {
			userConversations = (HashMap<String, Long>) PersistenceUtil.readObject(filePath);

			// ficheiro nao existe -> criar ficheiro
		} else {
			userConversations = new HashMap<String, Long>();
			PersistenceUtil.createFile(filePath);
		}

		// atualizar dados em memoria e em disco
		userConversations.put(toUser, conversationId);
		PersistenceUtil.writeObject(userConversations, filePath);
	}

	/**
	 * Obtem uma lista das ultimas mensagens de cada conversa do utilizador
	 * 
	 * @param conversationsIDs
	 *            Lista de IDs de cada conversa em que o utilizador participou
	 * @return Lista das ultimas mensagens
	 * @requires conversationId != null
	 */
	public ChatMessage getLastChatMessage(Long conversationId) {
		String path = "conversations/" + conversationId;
		Conversation lastConversation = (Conversation) PersistenceUtil.readObject(path + "/conversation");

		// nao existe ultima mensagem registada
		if (lastConversation == null)
			return null;

		// atualizar data da ultima mensagem enviada
		long date = lastConversation.getLastMessageDate().getTime();
		ArrayList<String> texto = (ArrayList<String>) PersistenceUtil.readFromFile(path + "/messages/" + date);
		ChatMessage lastMessage = makeChatMessage(texto);
		lastMessage.setCreatedAt(lastConversation.getLastMessageDate());

		return lastMessage;
	}

	/**
	 * Obtem uma conversa atraves do seu id
	 * 
	 * @param conversationId
	 *            Id da conversa a ser obtida
	 * @return Devolve a conversa correspondente ao id ou null caso esta nao
	 *         exista
	 * @requires conversationId != null
	 */
	private Conversation getConversationById(Long conversationId) {
		String path = "conversations/" + conversationId + "/conversation";
		File file = new File(path);

		// ficheiro nao existe
		if (!file.exists())
			return null;

		// obter conversa
		Conversation conversation = (Conversation) PersistenceUtil.readObject(path);

		return conversation;
	}

	/**
	 * Devolve uma lista de Ids de conversações que um dado user mantém
	 * 
	 * @param username
	 *            Nome do utilizador de quem se pretende as conversas
	 * @return Devolve uma lista de ids das conversacoes ou null caso nao
	 *         existam para este utilizador
	 * @requires username != null
	 */
	public ArrayList<Long> getAllConversationsFrom(String username) {
		String path = "users/" + username + "/conversations";
		HashMap<String, Long> conversations = (HashMap<String, Long>) PersistenceUtil.readObject(path);

		// obter os ids de todas as conversas do utilizador
		Collection<Long> collection = conversations.values();

		return new ArrayList<Long>(collection);
	}

	/**
	 * Obtem todas as mensagens associadas a uma conversação
	 * 
	 * @param consersationId
	 *            Identificador da conversa de onde se quer as mensagens
	 * @return Devolve uma lista de todas as mensagens sobre a forma de
	 *         ChatMessages
	 */
	public List<ChatMessage> getAllMessagesFromConversation(Long conversationId) {
		// pasta onde estao localizadas todas as mensagens da conversa
		String path = "conversations/" + conversationId + "/messages";
		File file = new File(path);

		ArrayList<ChatMessage> allMessages = new ArrayList<ChatMessage>();

		// obter lista de todos os ficheiros presentes no dir
		String[] filesIn = file.list();

		// percorrer todos os ficheiros das mensagens
		for (int i = 0; i < filesIn.length; i++) {
			// ler conteudo da mensagem
			ArrayList<String> texto = (ArrayList<String>) PersistenceUtil.readFromFile(path + "/" + filesIn[i]);
			ChatMessage k = makeChatMessage(texto);

			// definir data de inicio da conversa atraves do seu nome de
			// ficheiro
			k.setCreatedAt(new Date(Long.parseLong(filesIn[i])));
			allMessages.add(k);
		}

		return allMessages;

	}

	/**
	 * Cria uma chatMessage a partir de um ArrayList que contem os varios campos
	 * que compõem uma ChatMessage
	 * 
	 * @param messageFields
	 *            Lista de atributos da mensagem a ser criada
	 * @return Mensagem composta pelos vários atributos
	 * @requires messageFields != null
	 */
	private ChatMessage makeChatMessage(ArrayList<String> messageFields) {
		StringBuilder sb = new StringBuilder();

		// iterar atributos da mensagem
		for (int i = 0; i < messageFields.size(); i++) {
			if (i > 2)
				sb.append(messageFields.get(i));
		}

		// criar mensagem
		ChatMessage chatMessage = new ChatMessage(MessageType.valueOf(messageFields.get(2)));
		chatMessage.setContent(sb.toString());
		chatMessage.setFromUser(messageFields.get(0));
		chatMessage.setDestination(messageFields.get(1));

		return chatMessage;
	}

	/**
	 * Devolve o identificador da conversa entre dois utilizadores
	 * 
	 * @param user1
	 *            Utilizador que faz parte da conversa
	 * @param user2
	 *            Utilizador que faz parte da conversa
	 * @return Devolve o identificador da conversa ou -1 caso esta não exista
	 * @requires user1 != && user2 != null
	 */
	public Long getConversationInCommom(String user1, String user2) {
		// vai as conversaçoes do user1
		String path = "users/" + user1 + "/conversations";
		HashMap<String, Long> conversations = (HashMap<String, Long>) PersistenceUtil.readObject(path);

		if (conversations == null)
			return (long) -1;

		// verifica se existe uma conversaçao com o user2
		Long id = conversations.get(user2);

		return id == null ? (long) -1 : id;
	}

	/**
	 * Retorna um path caso exista um ficheiro na conversa entre 2 utilizadores
	 * 
	 * @param fromUser
	 *            Utilizador participante da conversa
	 * @param toUser
	 *            Utilizador participante da conversa
	 * @param fileName
	 *            Nome do ficheiro que se quer obter o path
	 * @return Devolve uma string com o caminho para o ficheiro ou null caso nao
	 *         exista
	 * @requires fromUser != null && toUser != null && fileName != null
	 */
	public String existFile(String fromUser, String toUser, String fileName) {
		Long id = getConversationInCommom(fromUser, toUser);

		// nao existe conversa em comum
		if (id == -1) {
			return null;

		} else {
			String path = "conversations/" + id + "/files/" + fileName;
			File f = new File(path);
			return f.exists() ? path : null;
		}
	}

	/**
	 * Remove uma conversa das lista de conversaçoes de username
	 * 
	 * @param username
	 *            Utilizador que quer ver removida conversa
	 * @param fromUser
	 *            Nome do utilizador com o qual username tem uma conversa
	 * @requires username != null && fromUser != null
	 */
	public void removeConversationFromUser(String username, String fromUser) {
		HashMap<String, Long> userConversations = null;
		String filePath = "users/" + username + "/conversations";
		File file = new File(filePath);

		// ficheiro existe e nao estah vazio
		if (file.exists() && file.length() != 0) {
			// remover utilizador e atualizar o ficheiro
			userConversations = (HashMap<String, Long>) PersistenceUtil.readObject(filePath);
			userConversations.remove(fromUser);
			PersistenceUtil.writeObject(userConversations, filePath);
		}
	}

	/**
	 * Remove uma conversa do ficheiro
	 * 
	 * @param conversationId
	 *            Identificador da conversa a ser removida
	 * @requires conversationId != null
	 */
	public void removeConversation(Long conversationId) {
		File file = new File("conversations/" + conversationId);
		PersistenceUtil.delete(file);
	}

	/**
	 * Obtem as pastas de todas as conversas registadas
	 * 
	 * @return Devolve todas as pastas das conversas
	 */
	public File[] getConversationsFolders() {
		File file = new File("conversations");

		if (!file.exists() || file.list().length == 0)
			return null;

		return file.listFiles();
	}

	/**
	 * Guarda a chave privada, enviada por um utilizador, associada a uma
	 * mensagem ou ficheiro esta irá ser guardada em fich.txt.key.FromUser e
	 * fich.txt.key.ToUser
	 * 
	 * @param username
	 * @param fileName
	 * @param privateKey
	 */
	public void saveUserFilePrivateKey(String username, String fileName, String privateKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("keys/");
		sb.append(fileName);
		sb.append(".key.");
		sb.append(username);

		PersistenceUtil.writeStringToFile(privateKey, sb.toString());
	}
}
