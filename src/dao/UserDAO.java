package dao;

import interfaces.dao.UserDAOInterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import util.PersistenceUtil;

/**
 * Classe que gere os utilizadores e que trata da sua persistencia no disco
 * 
 * @author António, José e Pedro
 *
 */
public class UserDAO implements UserDAOInterface {

	public UserDAO() {

	}

	/**
	 * Carrega em memória todos os utilizadores registados
	 * 
	 * @return Devolve um HashMap de utilizadores e suas palavra-passes
	 */
	@Override
	public ConcurrentHashMap<String, String> getUsers() {
		ConcurrentHashMap<String, String> users = new ConcurrentHashMap<String, String>();

		String line;
		BufferedReader br;

		// carregar utilizadores
		File file = new File("users.txt");

		// nao existe ficheiro
		if (!file.exists()) {
			System.out.println("Nao existem utilizadores adicionados.");
			return users;
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

				// criar pasta para o user
				PersistenceUtil.createDir("users/" + username);
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return users;
	}

	/**
	 * Função que permite adicionar um utilizador
	 * 
	 * @param username
	 *            Nome do utilizador
	 * @param password
	 *            Password do utilizador
	 * @return Devolve true caso o utilizador tenha sido adicionado e false caso
	 *         contrário
	 */
	@Override
	public void addUser(String username, String password) {
		if (username == null || password == null)
			return;

		// atualizar ficheiro
		try {
			FileWriter fw = new FileWriter("users.txt", true);
			fw.write(username + ":" + password + "\n");
			fw.close();

			// criar directorias
			PersistenceUtil.createFile("users/" + username + "/conversations");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Elimina um utilizador do ficheiro users.txt
	 * 
	 * @param username-
	 *            nome do utilizador a ser apagado
	 */
	@Override
	public void deleteUser(String username) {

		File file = new File("users/" + username + ".txt");
		File tempFile = new File("users/tempUser.txt");

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			String line;

			// copiar todos os users para o ficheiro temp
			while ((line = reader.readLine()) != null) {
				String currentUsername = line.split(" ")[0];

				// nao copiar user
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

		// eliminar pasta do utilizador
		file = new File("users/" + username);
		file.delete();
	}
}
