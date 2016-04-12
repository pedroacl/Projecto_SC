package service;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

import dao.UserDAO;
import interfaces.service.UserServiceInterface;
import security.Security;

public class UserService implements UserServiceInterface {
	
	private static UserDAO userDAO;

	private ConcurrentHashMap<String, String> users;

	public UserService() {
		userDAO = new UserDAO();
		users = userDAO.getUsers();
	}
	
	@Override
	public ConcurrentHashMap<String, String> getUsers() {
		return users;
	}

	@Override
	public void addUser(String username, String password) {
		int salt = Security.generateSalt();
		String passwordAndSalt = Integer.toString(salt) + password;
		byte[] hash = Security.getHash(passwordAndSalt.getBytes());
		
		users.put(username, new String(hash));
		userDAO.addUser(username, passwordAndSalt);
	}
	
	@Override
	public String[] getUserPasswordAndSalt(String username) {
		String passwordAndSalt = users.get(username);
		
		if (passwordAndSalt == null)
			return null;
		
		String[] passwordAndSaltArray = passwordAndSalt.split(":");
		
		return passwordAndSaltArray;
	}
}
