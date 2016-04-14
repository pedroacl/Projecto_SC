package service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

import dao.UserDAO;
import interfaces.service.UserServiceInterface;
import security.SecurityUtils;

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
		// adicionar salt ah password
		int salt = SecurityUtils.generateSalt();
		String saltString = Integer.toString(salt);
		String passwordAndSalt = saltString + password;

		// obter hash
		byte[] hash = SecurityUtils.getHash(passwordAndSalt);
		String hashString = String.format("%064x", new java.math.BigInteger(1, hash));

		// criar par salt:hashedPassword
		StringBuilder sb = new StringBuilder(); 
		sb.append(saltString);
		sb.append(":");
		sb.append(hashString);

		users.put(username, sb.toString());
		
		// persistir utilizador
		userDAO.addUser(username, sb.toString());
	}

	@Override
	public String[] getUserPasswordAndSalt(String username) {
		String passwordAndSalt = users.get(username);

		if (passwordAndSalt == null)
			return null;

		// salt:password
		// [0] -> salt
		// [1] -> password
		String[] passwordAndSaltArray = passwordAndSalt.split(":");

		return passwordAndSaltArray;
	}
}
