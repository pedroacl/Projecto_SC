package service;

import java.util.concurrent.ConcurrentHashMap;

import dao.UserDAO;
import util.MiscUtil;
import util.SecurityUtils;

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

	public void addUser(String username, String userPassword, String serverPassword) {
		// adicionar salt ah password
		int salt = SecurityUtils.generateSalt();
		String saltString = Integer.toString(salt);
		String passwordAndSalt = saltString + userPassword;

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
		System.out.println("Username = " + username);
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
