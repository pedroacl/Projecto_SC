package domain;

import java.util.HashMap;

import dao.GroupDAO;
import dao.UserDAO;
import interfaces.AuthenticationInterface;

public class Authentication implements AuthenticationInterface {

	private static Authentication authentication = new Authentication();

	private static UserDAO userDAO;

	private static GroupDAO groupDAO;

	private HashMap<String, String> users;

	// groupName:owner
	private HashMap<String, String> groups;

	private Authentication() {
		userDAO = UserDAO.getInstance();
		groupDAO = GroupDAO.getInstance();
		
		users = userDAO.getUsers();
		groups = groupDAO.getGroups();

		System.out.println("[Authentication.java]" + users);
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
		return users.get(username) != null;
	}

	/**
	 * 
	 * @param groupName
	 * @return
	 */
	@Override
	public boolean existsGroup(String groupName) {
		return groups.get(groupName) != null;
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

	/**
	 * 
	 */
	@Override
	public void addGroup(String groupName, String ownerName) {
		// TODO Auto-generated method stub
	}
}
