package persistence;

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

public class ConversationDAO {
	
	public ConversationDAO() {
		
	}
	
	/**
	 * Função que permite persistir uma determinada mensagem em disco
	 * @param chatMessage Mensagem a ser guardada
	 */
	public void addChatMessage(ChatMessage chatMessage) {

		ConversationFactory conversationFactory = ConversationFactory.getInstance();
		Conversation conversation = getConversationByChatMessage(chatMessage);
		File file;
		
		//criar conversa
		if (conversation == null) {
			conversation = conversationFactory.build(chatMessage.getFromUser(), chatMessage.getDestination());
			file = new File("conversations/" + conversation.getId());
			
			//pasta da conversation nao existe
			if (!file.exists()) {
				file.mkdir();						
			}			
		}
		
		//obter lista de chat messages
		try {
			//carregar ficheiro
			file = new File("conversations/" + conversation.getId() + "/messages");
			List<ChatMessage> chatMessages;
			
			if (!file.exists()) {
				file.createNewFile();
				chatMessages = new ArrayList<ChatMessage>();

			} else {
				FileInputStream fileInputStream = new FileInputStream(file);
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				chatMessages = (List<ChatMessage>) objectInputStream.readObject();
				
				objectInputStream.close();
				fileInputStream.close();
			}
			
			chatMessages.add(chatMessage);
		
			//atualizar ficheiro
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(chatMessages);

			objectOutputStream.close();
			fileOutputStream.close();
			
			//atualizar conversation headers de cada user
			updateUserConversationHeaders(conversation.getFromUser(), conversation);
			updateUserConversationHeaders(conversation.getToUser(), conversation);

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
	 * @param username
	 * @param conversation
	 */
	public void updateUserConversationHeaders(String username, Conversation conversation) {
		File file = new File("users/" + username + "/conversations");
		
		ConversationHeader conversationHeader = 
				new ConversationHeader(conversation.getId(), conversation.getToUser());
		
		//ficheiro nao existe
		if (!file.exists()) {
			//criar ficheiro
			try {
				file.createNewFile();
				
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

				//atualizar ficheiro
				List<ConversationHeader> conversationHeaders = new ArrayList<ConversationHeader>(); 
				conversationHeaders.add(conversationHeader);
				objectOutputStream.writeObject(conversationHeaders);
				
				objectOutputStream.close();
				fileOutputStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		//ficheiro ja existe
		//atualizar conversation headers do utilizador
		} else {
			//ler conversation headers do utilizador
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

				ArrayList<ConversationHeader> conversationHeaders = 
						(ArrayList<ConversationHeader>) objectInputStream.readObject();
				
				if (!conversationHeaders.contains(conversationHeader)) {
					conversationHeaders.add(conversationHeader);
				}
				
				objectInputStream.close();
				fileInputStream.close();
			
				//atualizar ficheiro
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(conversationHeaders);
				
				objectInputStream.close();
				fileOutputStream.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * 
	 * @param chatMessage
	 * @return
	 */
	public Conversation getConversationByChatMessage(ChatMessage chatMessage) {

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
				return getConversationById(conversationHeader.getId());
			}
		}
	
		return null;
	}
	


	/**
	 * Função que devolve uma conversa 
	 * @param conversationId ID da conversa
	 * @return Devolve uma conversa caso esta exista ou null caso contrário
	 */
	public Conversation getConversationById(Long conversationId) {
		File file = new File("conversations/" + conversationId);

		if (!file.exists()){
			return null;
		}
		
		Conversation conversation = new Conversation();
		
		try {
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fin);
			conversation = (Conversation) ois.readObject();
			ois.close();

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
	public List<ChatMessage> getLastChatMessages(List<Long> conversationsIDs) {
		List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
		ChatMessage chatMessage = null;
		File file = null;
		
		for (Long conversationID: conversationsIDs) {
			file = new File("conversations/" + conversationID);
		
			//saltar para o proximo ficheiro
			if (!file.exists()) {
				continue;
			}
			
			try {
				FileInputStream fin = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fin);
				chatMessage = (ChatMessage) ois.readObject();
				chatMessages.add(chatMessage);
				ois.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	

	/**
	 * Permite registar uma conversa com os seus utilizadores
	 * @param conversation
	 */
	public void addConversationToUsers(Conversation conversation) {
		
		addConversationToUser(conversation, conversation.getFromUser());
		addConversationToUser(conversation, conversation.getToUser());
	}
	
	
	/**
	 * 
	 * @param conversation
	 * @param username
	 */
	private void addConversationToUser(Conversation conversation, String username) {

		List<Conversation> conversations = new ArrayList<Conversation>();

		File file = new File("users/" + username + "/conversations");

		//utilizador ainda não tem conversas
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				conversations = (ArrayList<Conversation>) objectInputStream.readObject();
				
				fileInputStream.close();
				objectInputStream.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	
		//conversa ainda nao registada com o utilizador
		if (!conversations.contains(conversation)) {
			conversations.add(conversation);
		}
		
		//atualizar ficheiro
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(conversations);

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
