package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import entities.Conversation;
import entities.ConversationHeader;

public class UserConversationHeaderDAO {
	
	private static UserConversationHeaderDAO conversationHeader = new UserConversationHeaderDAO();
	
	
	private UserConversationHeaderDAO(){
		
	}
	
	public static UserConversationHeaderDAO getInstance() {
		return conversationHeader;
	}
	
	
	/**
	 * 
	 * @param username
	 * @param toUser
	 * @return
	 */
	public ConversationHeader getUserConversationHeader(String username, String toUser) {
		ArrayList<ConversationHeader> conversationHeaders = getUserConversationHeaders(username);
		
		for (ConversationHeader conversationHeader : conversationHeaders)
			if (conversationHeader.getToUser().equals(toUser)) 
				return conversationHeader;
		
		return null;
	}

	
	/**
	 * 
	 * @param username
	 * @return
	 */
	private ArrayList<ConversationHeader> getUserConversationHeaders(String username) {
		ArrayList<ConversationHeader> conversationHeaders = null;
				
		try {
			File file = new File("users/" + username + "/conversations");

			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			
			conversationHeaders = (ArrayList<ConversationHeader>) objectInputStream.readObject();
			
			objectInputStream.close();
			fileInputStream.close();
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		return conversationHeaders;
	}
	
	
	/**
	 * 
	 * @param username
	 * @param conversation
	 */
	public void addUserConversationHeader(String username, Conversation conversation) {
		File file = new File("users/" + username + "/conversations");
		
		ConversationHeader conversationHeader = 
				new ConversationHeader(conversation.getId(), conversation.getToUser());
		
		FileOutputStream fileOutputStream;
		ObjectOutputStream objectOutputStream;
		
		ArrayList<ConversationHeader> conversationHeaders = null;
		
		//ficheiro nao existe
		if (!file.exists()) {
			//criar ficheiro
			try {
				System.out.println("Criar ficheiro " + file.getAbsolutePath());
				file.getParentFile().mkdirs();
				file.createNewFile();
				
				conversationHeaders = new ArrayList<ConversationHeader>();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			conversationHeaders = getUserConversationHeaders(username);	
		} 
		
		//conversa ainda nao adicionada ao utilizador
		if (!conversationHeaders.contains(conversationHeader)) {
			//adicionar conversa
			conversationHeaders.add(conversationHeader);
		
			//atualizar ficheiro
			try {
				fileOutputStream = new FileOutputStream(file);
				objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(conversationHeaders);

				objectOutputStream.close();
				fileOutputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
