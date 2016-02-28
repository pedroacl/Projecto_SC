package persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import entities.Conversation;
import entities.User;

/**
 * 
 * @author pedro
 *
 */
public class UserDAO {
	
	private HashMap<String, String> users;
	
	public UserDAO() {
		loadUsers();
	}
	
	/**
	 * Função que permite obter um utilizador registado
	 * @param username Nome do utilizador
	 * @return Devolve um utilizador caso este exista ou null caso contrário
	 */
	public User getUser(String username) {
		if (username == null)
			return null;
		
		String password = users.get(username);
		User user = null; 

		if (password != null) {
			user = new User(username, password);
		}
		
		return user;
	}
	
	/**
	 * Função que permite adicionar um utilizador 
	 * @param username Nome do utilizador
	 * @param password Password do utilizador
	 * @return Devolve true caso o utilizador tenha sido adicionado e false caso contrário
	 */
	public boolean addUser(String username, String password) {
		if (username == null || password == null)		
			return false;
		
		User user = getUser(username);
		
		//user ja existe
		if (user != null)
			return false;

		//adicionar user
		users.put(username, password);
	
		//atualizar ficheiro
		try {
			FileWriter fw = new FileWriter("users.txt", true);
			fw.write(username + " " + password + "\n");
			fw.close();
		
			//criar directorias
			File file = new File("users/" + username + "/files");
			file.mkdir();
			
			//cirar ficheiro de conversas
			file = new File("users/" + username + "/conversations");
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return true;
	}
	
	/**
	 * 
	 * @param username
	 */
	public void removeUser(String username) {
		users.remove(username);
		
		File file = new File("users/" + username + ".txt");
		File tempFile = new File("users/tempUser.txt");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			
			String line;
			
			//copiar todos os users para o ficheiro temp
			while((line = reader.readLine()) != null) {
				String currentUsername = line.split(" ")[0];

				//nao copiar user
				if (currentUsername.equals(username))
					continue;
					
			    writer.write(line);
			}
			
			writer.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Função que carrega em memória todos os utilizadores registados
	 * @return 
	 */
	private void loadUsers() {
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

		try {
			FileReader fr = new FileReader(file);
			
			br = new BufferedReader(fr);
			
			while ((line = br.readLine()) != null) {
				String[] args = line.split(" ");
				String username = args[0];
				String password = args[1];
				
				users.put(username, password);
				System.out.println(line);
				
				//criar pasta para o user
				file = new File("users/" + username);

				if (!(file.exists() && file.isDirectory())) {
					file.mkdir();
				}
				
				file = new File("users/" + username + "/conversations");
				
				if (!file.exists()) {
					file.createNewFile();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
