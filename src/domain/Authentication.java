package domain;

import java.util.ArrayList;

import entities.User;

public class Authentication {
	
	private ArrayList<User> users;

	public Authentication() {
		
	}
	
	public Authentication(ArrayList<User> users) {
		this.users = users;
	}
	
	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}
	
	public boolean authenticateUser(String username, String password) {
		
		for (User currentUser : users) {
			//user registado
			if (currentUser.getUsername().equals(username)) {
				//password valida
				if (currentUser.getPassword().equals(password)) {
					return true;
				}
				
				return false;
			}
		}
		
		return false;
	} 
}
