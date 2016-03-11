package service;

import java.util.HashMap;

import dao.UserDAO;
import interfaces.service.UserServiceInterface;

public class UserService implements UserServiceInterface {
	
	private static UserDAO userDAO;

	private HashMap<String, String> users;

	public UserService() {
		userDAO = UserDAO.getInstance();
		users = userDAO.getUsers();
	}
	
	public HashMap<String, String> getUsers() {
		return users;
	}

	public void addUser(String username, String password) {
		users.put(username, password);
		userDAO.addUser(username, password);
	}

	public String getUserPassword(String username) {
		return users.get(username);
	}
}
