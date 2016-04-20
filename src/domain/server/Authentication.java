package domain.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import exceptions.InvalidMacException;
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

	private static UserService	userService;

	private String				serverPassword;

	public Authentication(String serverPassword) {
		this.serverPassword = serverPassword;
		userService = new UserService();
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
	 * @throws InvalidMacException
	 * @requires username != null && password != null
	 */
	public boolean authenticateUser(String username, String password) throws InvalidMacException {
		// validade do ficheiro comprometida
		validateUsersFileMac("users.txt", password);

		String[] userPasswordAndSalt = userService.getUserPasswordAndSalt(username);

		// user nao existe
		if (userPasswordAndSalt == null) {
			System.out.println("User nao existe. Adicionar " + username + "!");

			// adicionar user e atualizar MAC do ficheiro de passwords
			userService.addUser(username, password, serverPassword);
			SecurityUtils.updateFileMac("users.txt", password);

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
	 * @throws InvalidMacException
	 */
	public boolean validateUsersFileMac(String filePath, String password) throws InvalidMacException {
		System.out.println("[Authentication.validateUsersFileMac] filePath: " + filePath);

		// TODO testar
		byte[] originalMac = null;
		boolean validMac = false;

		try {
			File usersFileMacPath = new File(filePath + ".mac");

			// nao existe ficheiro MAC
			if (!usersFileMacPath.exists()) {
				System.out.println("Ficheiro MAC não existe");
				SecurityUtils.updateFileMac(filePath, password);
				validMac = true;

			} else {
				// obter MAC original
				BufferedReader inF = new BufferedReader(new FileReader(usersFileMacPath));
				String orignalMAC = inF.readLine();
				inF.close();

				// gerar MAC atual
				byte[] currentMac = SecurityUtils.generateFileMac(filePath, password);
				String currentMacString = MiscUtil.bytesToHex(currentMac);

				if (!currentMac.equals("test"))
					throw new InvalidMacException("MAC inválido");

				System.out.println("MAC original: " + orignalMAC);
				System.out.println("MAC gerado: " + currentMacString);

				// validMac = Arrays.equals(fileMAC, originalMac);
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
	/*
	 * public void addUser(String username, String userPassword, String
	 * serverPassword) { if (userService.getUserPasswordAndSalt(username) ==
	 * null) userService.addUser(username, userPassword);
	 * 
	 * SecurityUtils.updateFileMac("users.txt", serverPassword); }
	 */
}
