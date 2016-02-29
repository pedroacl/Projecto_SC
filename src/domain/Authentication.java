package domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import entities.User;
import persistence.UserDAO;

public class Authentication {
	
	private static UserDAO userDAO;

	private HashMap<String, String> users;
	
	private static Authentication authentication = new Authentication();
	

	private Authentication() {
		userDAO = UserDAO.getInstance();
		users = loadUsers();
		
		System.out.println(users);
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
	public boolean authenticateUser(String username, String password) {
		
		String userPassword = users.get(username);
	
		//user nao existe
		if (userPassword == null) {
			userDAO.addUser(username, password);
			users.put(username, password);
		}
		//user existe e a password eh invalida
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
	public boolean exists(String username) {
		return users.get(username) != null;
	}


	/**
	 * 
	 * @param username
	 * @param password
	 */
	public void addUser(String username, String password) {
		if (users.get(username) != null) {
			users.put(username, password);
			userDAO.addUser(username, password);
		}
	}
	
	
	/**
	 * 
	 * @param username
	 */
	public void deleteUser(String username) {
		if (users.remove(username) != null) {
			userDAO.deleteUser(username);
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	public User getUser(String username) {
		String password = users.get(username);
		
		return new User(username, password);
	}
	
	
	/**
	 * Função que carrega em memória todos os utilizadores registados
	 * @return 
	 */
	private HashMap<String, String> loadUsers() {
		users = new HashMap<String, String>();
		
		String line;
		BufferedReader br;

		//criar ficheiro users
		File file = new File("users.txt");
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//carregar utilizadores
		file = new File("users.txt");
	
		//nao existe ficheiro
		if (!file.exists()) {
			System.out.println("Nao existem utilizadores adicionados.");
		}

		try {
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			while ((line = br.readLine()) != null) {
				String[] args = line.split(":");
				String username = args[0];
				String password = args[1];
				
				users.put(username, password);
				System.out.println(username + " " + password);
		
				//criar pasta para o user
				file = new File("users/" + username);

				if (!(file.exists() && file.isDirectory())) {
					file.mkdir();
				}	
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return users;
	}
}
