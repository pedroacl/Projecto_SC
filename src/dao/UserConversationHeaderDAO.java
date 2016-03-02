package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import entities.Conversation;
import entities.UserConversationHeader;
import interfaces.dao.UserConversationHeaderDAOInterface;

public class UserConversationHeaderDAO implements UserConversationHeaderDAOInterface {

	private static UserConversationHeaderDAO conversationHeader = new UserConversationHeaderDAO();

	private UserConversationHeaderDAO() {

	}

	public static UserConversationHeaderDAO getInstance() {
		return conversationHeader;
	}

	/**
	 * Funcao que permite adicionar conversation headers a cada utilizador
	 * presente na conversa
	 */
	@Override
	public void addUsersConversationHeaders(Conversation conversation) {
		addUserConversationHeader(conversation.getFromUser(), conversation);
		addUserConversationHeader(conversation.getToUser(), conversation);
	}

	/**
	 * Função que permite adicionar a um utilizador da conversa um conversation header
	 * @param username
	 * @param conversation
	 */
	private void addUserConversationHeader(String username, Conversation conversation) {

		FileOutputStream fileOutputStream;
		ObjectOutputStream objectOutputStream;

		// obter conversation headers do utilizador
		HashMap<String, UserConversationHeader> userConversationHeaders = getUserConversationHeaders(username);
		
		if (userConversationHeaders == null)
			userConversationHeaders = new HashMap<String, UserConversationHeader>();

		File file = new File("users/" + username + "/conversations");

		// ficheiro nao existe
		if (!file.exists()) {
			// criar ficheiro
			try {
				System.out.println("Criar ficheiro " + file.getAbsolutePath());
				file.getParentFile().mkdirs();
				file.createNewFile();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// adicionar/atualizar conversation header
		userConversationHeaders.put(conversation.getToUser(), new UserConversationHeader(conversation.getToUser(), conversation.getId()));

		// atualizar ficheiro
		try {
			fileOutputStream = new FileOutputStream(file);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(userConversationHeaders);

			objectOutputStream.close();
			fileOutputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param username
	 * @return
	 */
	private HashMap<String, UserConversationHeader> getUserConversationHeaders(String username) {
		HashMap<String, UserConversationHeader> userConversationHeaders = null;

		File file = new File("users/" + username + "/conversations");

		if (!file.exists()) {
			return null;
		}

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

			userConversationHeaders = (HashMap<String, UserConversationHeader>) objectInputStream.readObject();

			objectInputStream.close();
			fileInputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return userConversationHeaders;
	}

	@Override
	public Long getConversationId(String fromUser, String toUser) {
		HashMap<String, UserConversationHeader> userConversationHeaders = getUserConversationHeaders(fromUser);

		if (userConversationHeaders == null) {
			return null;
		}
		
		UserConversationHeader userConversationHeader = userConversationHeaders.get(toUser);

		if (userConversationHeader != null) {
			return userConversationHeader.getConversationId();
		}

		return null;
	}

	@Override
	public UserConversationHeader getUserConversationHeader(String username, String toUser) {
		HashMap<String, UserConversationHeader> userConversationHeader = getUserConversationHeaders(username);

		if (userConversationHeader != null) 
			return userConversationHeader.get(toUser);
		
		return null;
	}
}
