package domain;

import java.util.HashMap;

import dao.UserDAO;
import entities.User;

public class Authentication {
	
	private static Authentication authentication = new Authentication();

	private static UserDAO userDAO;

	private HashMap<String, String> users;
	

	private Authentication() {
		userDAO = UserDAO.getInstance();
		users = userDAO.getUsers();
		
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
	public boolean authenticateUser(String username, String password) {
		
		String userPassword = users.get(username);
	
		//user nao existe
		if (userPassword == null) {
			userDAO.addUser(username, password);
			users.put(username, password);
		}
		//user existe e a password eh invalida
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
	public boolean exists(String username) {
		return users.get(username) != null;
	}


	/**
	 * 
	 * @param username
	 * @param password
	 */
	public void addUser(String username, String password) {
		if (users.get(username) == null) {
			users.put(username, password);
			userDAO.addUser(username, password);
		}
	}
	
	
	/**
	 * 
	 * @param username
	 */
	public void deleteUser(String username) {
		if (users.remove(username) != null) {
			userDAO.deleteUser(username);
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	public User getUser(String username) {
		User user = null;
		String password = users.get(username);
		
		if (password != null)
			user = new User(username, password);
		
		return user;
	}
}
