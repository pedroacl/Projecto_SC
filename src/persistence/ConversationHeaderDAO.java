package persistence;

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

public class ConversationHeaderDAO {
	
	private static ConversationHeaderDAO conversationHeader = new ConversationHeaderDAO();
	
	
	private ConversationHeaderDAO(){
		
	}
	
	
	public static ConversationHeaderDAO getInstance() {
		return conversationHeader;
	}

	
	/**
	 * 
	 * @param username
	 * @return
	 */
	ArrayList<ConversationHeader> getUserConversationHeaders(String username) {
		File file = new File("users/" + username + "/conversations");
		ArrayList<ConversationHeader> conversationHeaders = null;
				
		try {
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
