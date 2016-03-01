package persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import domain.Authentication;
import entities.User;
import factories.ConversationFactory;

/**
 * 
 * @author pedro
 *
 */
public class UserDAO {
	
	private static UserDAO userDAO = new UserDAO();
	
	private UserDAO() {

	}
	
	
	public static UserDAO getInstance() {
		return userDAO;
	}
	
	
	/**
	 * Função que carrega em memória todos os utilizadores registados
	 * @return 
	 */
	public HashMap<String, String> getUsers() {
		HashMap<String, String> users = new HashMap<String, String>();
		
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
	
	
	/**
	 * Função que permite adicionar um utilizador 
	 * @param username Nome do utilizador
	 * @param password Password do utilizador
	 * @return Devolve true caso o utilizador tenha sido adicionado e false caso contrário
	 */
	public void addUser(String username, String password) {
		if (username == null || password == null)		
			return;
		
		//atualizar ficheiro
		try {
			FileWriter fw = new FileWriter("users.txt", true);
			fw.write(username + ":" + password + "\n");
			fw.close();
		
			//criar directorias
			File file = new File("users/" + username + "/files");
			file.getParentFile().mkdirs();
			file.createNewFile();
			
			//cirar ficheiro de conversas
			file = new File("users/" + username + "/conversations");
			file.getParentFile().mkdirs();
			file.createNewFile();

		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	
	/**
	 * 
	 * @param username
	 */
	public void deleteUser(String username) {
		
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
	
		//eliminar pasta do utilizador
		file = new File("users/" + username);
		file.delete();
	}
}
