package domain.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import exceptions.InvalidMacException;
import exceptions.InvalidPasswordException;
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

	private static final String	USERS_FILE_PATH	= "users.txt";

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
	 * @throws InvalidPasswordException
	 * @requires username != null && password != null
	 */
	public void authenticateUser(String username, String password)
			throws InvalidMacException, InvalidPasswordException {

		// validade do ficheiro comprometida
		String filePath = USERS_FILE_PATH;
		File file = new File(filePath);

		String[] userPasswordAndSalt = null;

		// ficheiros users.txt nao existe
		if (!file.exists()) {
			System.out.println("Nao existe ficheiro users");

			// criar ficheiro e adicionar user
			userService.addUser(username, password, serverPassword);
			
			// gerar MAC file
			SecurityUtils.generateFileMac(filePath, serverPassword);
		}
		// user nao existe
		else if ((userPasswordAndSalt = userService.getUserPasswordAndSalt(username)) == null) {
			System.out.println("User nao existe. Adicionar " + username + "!");

			validateUsersFileMac(filePath, password);

			// adicionar user e atualizar MAC do ficheiro de passwords
			userService.addUser(username, password, serverPassword);

			// atualizar MAC do ficheiro
			SecurityUtils.updateFileMac("users.txt", password);

		} else {
			System.out.println("Authentication - User existe!");

			validateUsersFileMac(filePath, password);

			byte[] passwordHash = SecurityUtils.getHash(userPasswordAndSalt[0] + password);
			String hashString = MiscUtil.bytesToHex(passwordHash);

			System.out.println("Hash guardada: " + userPasswordAndSalt[1]);
			System.out.println("Hash gerada:   " + hashString);

			if (!userPasswordAndSalt[1].equals(hashString)) {
				throw new InvalidPasswordException();
			}
		}

	}

	/**
	 * 
	 * @param usersFilePath
	 * @param password
	 * @return
	 * @throws InvalidMacException
	 */
	public void validateUsersFileMac(String filePath, String password) throws InvalidMacException {
		System.out.println("[Authentication.validateUsersFileMac] filePath: " + filePath);

		try {
			File usersFileMacPath = new File(filePath + ".mac");

			// nao existe ficheiro MAC
			if (!usersFileMacPath.exists()) {
				System.out.println("Ficheiro MAC não existe");
				SecurityUtils.updateFileMac(filePath, password);

			} else {
				System.out.println("[Authentication.validateUsersFileMac] ficheiro MAC existe");

				// obter MAC original
				BufferedReader inF = new BufferedReader(new FileReader(usersFileMacPath));
				String originalMAC = inF.readLine();
				inF.close();

				// gerar MAC atual
				System.out.println(filePath);
				System.out.println(password);
				String currentMacString = MiscUtil.bytesToHex(SecurityUtils.generateFileMac(filePath, password));

				System.out.println("MAC original: " + originalMAC);
				System.out.println("MAC gerado: " + currentMacString);

				if (!originalMAC.equals(currentMacString)) {
					throw new InvalidMacException();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
