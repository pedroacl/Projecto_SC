package domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import entities.User;
import factories.UserFactory;

public class Authentication {
	
	private HashMap<String, User> users;

	public Authentication() {
		users = new HashMap<String, User>();
		loadUsers();
	}

	
	public boolean authenticateUser(String username, String password) {
		
		User user = users.get(username);
		
		if (user != null && user.getPassword().equals(password)) {
			return true;
		}

		return false;
	}
	
	
	private void loadUsers() {
		FileInputStream in;
		ObjectInputStream oin;
		
		String line;
		BufferedReader br;

		//criar ficheiro users
		File file = new File("users");
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		//carregar utilizadores
		file = new File("users");

		try {
			FileReader fr = new FileReader(file);
			
			br = new BufferedReader(fr);
			UserFactory userFactory = new UserFactory();
			
			while ((line = br.readLine()) != null) {
				String[] args = line.split(" ");
				String username = args[0];
				String password = args[1];
				
				User user = userFactory.build(username, password);
				
				users.put(username, user);
				System.out.println(line);
				
				//criar pasta para o user
				file = new File("users/" + user.getId());

				if (!(file.exists() && file.isDirectory())) {
					file.mkdir();
				}
				
				file = new File("users/" + user.getId() + "/conversations");
				
				if (!file.exists()) {
					file.createNewFile();
				}
			}
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
