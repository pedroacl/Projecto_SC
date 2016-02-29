package domain;

import entities.User;
import persistence.UserDAO;

public class Authentication {
	
	private UserDAO userDAO;
	
	private static Authentication authentication = new Authentication();
	

	private Authentication() {
		userDAO = new UserDAO();
	}
	
	
	public static Authentication getInstance() {
		return authentication;
	}

	
	public boolean authenticateUser(String username, String password) {
		
		User user = userDAO.getUser(username);
	
		//user nao existe
		if (user == null) {
			userDAO.addUser(username, password);
		}
		//user existe e a password eh invalida
		else if (!user.getPassword().equals(password)) {
			return false;
		}

		return true;
	}


	public boolean exists(String username) {
		return userDAO.getUser(username) != null;
	}
}
