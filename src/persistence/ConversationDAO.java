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

public class ConversationDAO {
	
	public ConversationDAO() {
		
	}
	
	/**
	 * Função que permite persistir uma determinada mensagem
	 * @param chatMessage Mensagem a ser persistida
	 */
	public void addChatMessage(Long conversationId, ChatMessage chatMessage) {
		File file = new File("conversations/" + conversationId);
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//obter lista de chat messages
		try {
			//carregar ficheiro
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			List<ChatMessage> chatMessages = (List<ChatMessage>) objectInputStream.readObject();
			
			//ficheiro vazio
			if (chatMessages == null) {
				chatMessages = new ArrayList<ChatMessage>();
			}
			
			chatMessages.add(chatMessage);
			
			objectInputStream.close();
			fileInputStream.close();
		
			//atualizar ficheiro
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(chatMessages);

			objectOutputStream.close();
			fileOutputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Função que devolve os IDs de todas as conversas de um utilizador
	 * @param username
	 * @return Devolve uma lista de IDs
	 */
	public List<Long> getUserConversationsIds(String username) {
		File file = new File("users/conversations");
		
		if (!file.exists()) {
			return null;
		}
		
		FileInputStream fileInputStream = null;
		List<Long> conversationsIds = null;
		
		try {
			fileInputStream = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fileInputStream);
			conversationsIds = (List<Long>) ois.readObject();
			ois.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		return conversationsIds;
	}


	/**
	 * Função que devolve uma conversa 
	 * @param conversationId ID da conversa
	 * @return Devolve uma conversa caso esta exista ou null caso contrário
	 */
	public List<ChatMessage> getConversation(Long conversationId) {
		File file = new File("conversations/" + conversationId);

		if (!file.exists()){
			return null;
		}
		
		List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
		ChatMessage chatMessage = null;
		
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
		
		return chatMessages;
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
}