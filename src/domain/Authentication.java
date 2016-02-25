package domain;

import entities.User;
import persistence.UserDAO;

public class Authentication {
	
	private UserDAO userDAO;

	public Authentication() {
		userDAO = new UserDAO();
	}

	
	public boolean authenticateUser(String username, String password) {
		
		User user = userDAO.getUser(username);
		
		if (user != null && user.getPassword().equals(password)) {
			return true;
		}

		return false;
	}
}
