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
	
	private static Authentication authentication;
	
	
	private UserDAO() {
		authentication = Authentication.getInstance();
	}
	
	
	public static UserDAO getInstance() {
		return userDAO;
	}
	
	
	/**
	 * Função que permite obter um utilizador registado
	 * @param username Nome do utilizador
	 * @return Devolve um utilizador caso este exista ou null caso contrário
	 */
	public User getUser(String username) {
		return authentication.getUser(username);
	}
	
	
	/**
	 * Função que permite adicionar um utilizador 
	 * @param username Nome do utilizador
	 * @param password Password do utilizador
	 * @return Devolve true caso o utilizador tenha sido adicionado e false caso contrário
	 */
	public void addUser(String username, String password) {
		System.out.println(authentication == null);
		
		if (username == null || password == null || authentication.getUser(username) == null)		
			return;
		
		//atualizar ficheiro
		try {
			FileWriter fw = new FileWriter("users.txt", true);
			fw.write(username + ":" + password + "\n");
			fw.close();
		
			//criar directorias
			File file = new File("users/" + username + "/files");
			file.mkdir();
			
			//cirar ficheiro de conversas
			file = new File("users/" + username + "/conversations");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	
	/**
	 * 
	 * @param username
	 */
	public void deleteUser(String username) {
		authentication.deleteUser(username);
		
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
