package domain.server;

import java.io.File;

import exceptions.InvalidMacException;
import exceptions.InvalidPasswordException;
import service.UserService;
import util.MiscUtil;
import util.SecurityUtils;

/**
 * <<SINGLETON>> Classe que verifica existencia dos utilizadores e que trata da
 * sua autenticaçao
 * 
 * @author António, José e Pedro
 *
 */
public class Authentication {

	private static final String USERS_FILE_PATH = "users.txt";

	private static UserService userService;

	private String serverPassword;

	public Authentication(String serverPassword) {
		this.serverPassword = serverPassword;
		userService = new UserService();
	}

	public String getServerPassword() {
		return this.serverPassword;
	}

	/**
	 * Adiciona um utilizador ao sistema caso este não exista. Se o utilizador
	 * existir verifica se a password corresponde ah respectiva sintese guardada
	 * 
	 * @param username
	 *            Nome do utilizador a autenticar
	 * @param userPassword
	 *            Palavra passe do servidor passada por argumentos na linha de
	 *            comandos
	 * @return False caso a password esteja errada
	 * @throws InvalidMacException
	 * @throws InvalidPasswordException
	 * @requires username != null && password != null
	 */
	public void authenticateUser(String username, String userPassword)
			throws InvalidMacException, InvalidPasswordException {

		// validade do ficheiro comprometida
		String filePath = USERS_FILE_PATH;
		File file = new File(filePath);

		String[] userPasswordAndSalt = null;

		// ficheiros users.txt nao existe
		if (!file.exists()) {
			System.out.println("Nao existe ficheiro users");

			// criar ficheiro e adicionar user
			userService.addUser(username, userPassword, serverPassword);

			// gerar MAC file
			SecurityUtils.generateFileMac(filePath, serverPassword);
		}
		// user nao existe
		else if ((userPasswordAndSalt = userService.getUserPasswordAndSalt(username)) == null) {
			System.out.println("User nao existe. Adicionar " + username + "!");

			SecurityUtils.validateFileMac(filePath, serverPassword);

			// adicionar user e atualizar MAC do ficheiro de passwords
			userService.addUser(username, userPassword, serverPassword);

			// atualizar MAC do ficheiro
			SecurityUtils.updateFileMac("users.txt", serverPassword);

		} else {
			System.out.println("Authentication - User existe!");

			SecurityUtils.validateFileMac(filePath, serverPassword);

			byte[] passwordHash = SecurityUtils.getHash(userPasswordAndSalt[0] + userPassword);
			String hashString = MiscUtil.bytesToHex(passwordHash);

			System.out.println("Hash guardada: " + userPasswordAndSalt[1]);
			System.out.println("Hash gerada:   " + hashString);

			if (!userPasswordAndSalt[1].equals(hashString)) {
				throw new InvalidPasswordException();
			}
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
