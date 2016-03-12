package service;

import java.util.HashMap;

import dao.UserDAO;
import interfaces.service.UserServiceInterface;

public class UserService implements UserServiceInterface {
	
	private static UserDAO userDAO;

	private HashMap<String, String> users;

	public UserService() {
		userDAO = new UserDAO();
		users = userDAO.getUsers();
	}
	
	@Override
	public HashMap<String, String> getUsers() {
		return users;
	}

	@Override
	public void addUser(String username, String password) {
		users.put(username, password);
		userDAO.addUser(username, password);
	}

	@Override
	public String getUserPassword(String username) {
		return users.get(username);
	}
}
