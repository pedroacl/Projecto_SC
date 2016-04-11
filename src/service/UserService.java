package service;

import java.math.BigInteger;
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
		final SecureRandom randomNumber = new SecureRandom();
		String salt = new BigInteger(130, randomNumber).toString(32);

		String passwordAndSalt = salt + ":" + (Security.getHash((salt + password).getBytes())).toString();
		users.put(username, passwordAndSalt);
		userDAO.addUser(username, passwordAndSalt);
	}
	
	@Override
	public String[] getUserPasswordAndSalt(String username) {
		String passwordAndSalt = users.get(username);
		
		//username:salt:password_hash
		String[] passwordAndSaltArray = passwordAndSalt.split(":");
		
		return passwordAndSaltArray;
	}
}
