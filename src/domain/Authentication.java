package domain;

import java.util.HashMap;

import dao.ConversationDAO;
import dao.GroupDAO;
import dao.UserDAO;
import interfaces.AuthenticationInterface;

public class Authentication implements AuthenticationInterface {

	private static Authentication authentication = new Authentication();

	private static UserDAO userDAO;

	private static GroupDAO groupDAO;

	private static ConversationDAO conversationDAO;

	private HashMap<String, String> users;

	private HashMap<String, String> groups; // groupName:owner

	private Authentication() {
		userDAO = UserDAO.getInstance();
		groupDAO = GroupDAO.getInstance();
		conversationDAO = ConversationDAO.getInstance();

		users = userDAO.getUsers();
		groups = groupDAO.getGroups();

		System.out.println("[Authentication.java] Users: " + users);
		System.out.println("[Authentication.java] Groups: " + groups);
	}

	public static Authentication getInstance() {
		return authentication;
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public boolean authenticateUser(String username, String password) {

		String userPassword = users.get(username);

		// user nao existe
		if (userPassword == null) {
			userDAO.addUser(username, password);
			users.put(username, password);
		}
		// user existe e a password eh invalida
		else if (!userPassword.equals(password)) {
			return false;
		}

		return true;
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	@Override
	public boolean existsUser(String username) {
		System.out.println("[Authentication]"+ users);
		System.out.println("[Authentication]: TRue? "+ (users.get(username) != null));
		return users.get(username) != null;
	}

	/**
	 * 
	 * @param username
	 * @param password
	 */
	@Override
	public void addUser(String username, String password) {
		if (users.get(username) == null) {
			users.put(username, password);
			userDAO.addUser(username, password);
		}
	}
}
