package service;

import java.util.concurrent.ConcurrentHashMap;

import dao.UserDAO;
import security.SecurityUtils;
import util.MiscUtil;

public class UserService {

	private static UserDAO userDAO;

	private ConcurrentHashMap<String, String> users;

	public UserService() {
		userDAO = new UserDAO();
		users = userDAO.getUsers();
	}

	public ConcurrentHashMap<String, String> getUsers() {
		return users;
	}

	public void addUser(String username, String password, String serverPassword) {
		// adicionar salt ah password
		int salt = SecurityUtils.generateSalt();
		String saltString = Integer.toString(salt);
		String passwordAndSalt = saltString + password;

		// obter hash
		byte[] hash = SecurityUtils.getHash(passwordAndSalt);
		String hashString = MiscUtil.bytesToHex(hash);

		// criar par salt:hashedPassword
		StringBuilder sb = new StringBuilder(); 
		sb.append(saltString);
		sb.append(":");
		sb.append(hashString);

		users.put(username, sb.toString());
		
		// persistir utilizador
		userDAO.addUser(username, sb.toString(), serverPassword);
	}

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
