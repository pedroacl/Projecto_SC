package domain;

import interfaces.AuthenticationInterface;
import service.UserService;

public class Authentication implements AuthenticationInterface {

	private static Authentication authentication = new Authentication();

	private static UserService userService;

	private Authentication() {
		userService = new UserService();
		System.out.println("[Authentication.java] Users: " + userService.getUsers());
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

		String userPassword = userService.getUserPassword(username);

		// user nao existe
		if (userPassword == null) {
			userService.addUser(username, password);
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
		return userService.getUserPassword(username) != null;
	}

	/**
	 * 
	 * @param username
	 * @param password
	 */
	@Override
	public void addUser(String username, String password) {
		if (userService.getUserPassword(username) == null) {
			userService.addUser(username, password);
		}
	}
}
