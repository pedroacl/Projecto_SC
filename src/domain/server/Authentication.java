package domain.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import security.SecurityUtils;
import service.UserService;
import util.MiscUtil;

/**
 * <<SINGLETON>> Classe que verifica existencia dos utilizadores e que trata da
 * sua autenticaçao
 * 
 * @author António, José e Pedro
 *
 */
public class Authentication {

	private static final String SERVER_PASSWORD = "password";

	private static Authentication authentication = new Authentication();

	private static UserService userService;

	private Authentication() {
		userService = new UserService();
	}

	/**
	 * Obtem uma instancia do singleton
	 * 
	 * @return Authentication
	 */
	public static Authentication getInstance() {
		return authentication;
	}

	/**
	 * Adiciona um utilizador ao sistema caso este não exista. Se o utilizador
	 * existir verifica se a password corresponde ah respectiva sintese guardada
	 * 
	 * @param username
	 *            Nome do utilizador a autenticar
	 * @param password
	 *            Palavra passe do servidor passada por argumentos na linha de
	 *            comandos
	 * @return False caso a password esteja errada
	 * @requires username != null && password != null
	 */
	public boolean authenticateUser(String username, String password) {
		// validade do ficheiro comprometida
		if (!validateUsersFileMac("users.txt", password))
			return false;

		String[] userPasswordAndSalt = userService.getUserPasswordAndSalt(username);

		// user nao existe
		if (userPasswordAndSalt == null) {
			System.out.println("User nao existe. Adicionar " + username + "!");

			// adicionar user e atualizar MAC do ficheiro de passwords
			userService.addUser(username, password);
			SecurityUtils.updateFileMac("users.txt.mac", password);
		} else {
			System.out.println("Authentication - User existe!");
			byte[] passwordHash = SecurityUtils.getHash(userPasswordAndSalt[0] + password);
			String hashString = MiscUtil.bytesToHex(passwordHash);

			System.out.println("Hash guardada: " + userPasswordAndSalt[1]);
			System.out.println("Hash gerada:   " + hashString);

			return userPasswordAndSalt[1].equals(hashString);
		}

		return false;
	}

	/**
	 * 
	 * @param usersFilePath
	 * @param password
	 * @return
	 */
	public boolean validateUsersFileMac(String usersFilePath, String password) {
		// TODO testar
		byte[] originalMac = null;
		boolean validMac = false;

		try {
			File usersMACFile = new File(usersFilePath + ".mac");

			// nao existe ficheiro MAC
			if (!usersMACFile.exists()) {
				SecurityUtils.updateFileMac(usersFilePath, password);
				validMac = true;

			} else {
				// obter MAC original
				BufferedReader inF = new BufferedReader(new FileReader(usersFilePath + ".mac"));
				String orignalMACString = inF.readLine();
				inF.close();

				originalMac = orignalMACString.getBytes();

				// gerar MAC atual
				byte[] fileMAC = SecurityUtils.generateFileMac(usersFilePath, password);
				validMac = Arrays.equals(fileMAC, originalMac);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return validMac;
	}

	/**
	 * Verifica se existe um utilizador
	 * 
	 * @param username
	 *            Nome do utilizador a verificar da sua existencia
	 * @param username
	 *            Nome do utilizador a verificar da sua existencia
	 * @return True caso utilizador exista, false caso contrario
	 * @requires username != null
	 */
	public boolean existsUser(String username) {
		return userService.getUserPasswordAndSalt(username) != null;
	}

	/**
	 * Adiciona um utilizador ao sistema
	 * 
	 * @param username
	 *            Utilizador a ser adicionado
	 * @param password
	 *            Palavra passe do utilizador
	 * @requires username != null && password != null
	 */
	public void addUser(String username, String password) {
		if (userService.getUserPasswordAndSalt(username) == null)
			userService.addUser(username, password);

		SecurityUtils.updateFileMac("users.txt", SERVER_PASSWORD);
	}
}
