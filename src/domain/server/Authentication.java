package domain.server;

import interfaces.AuthenticationInterface;
import security.Security;
import service.UserService;

/**
 * <<SINGLETON>> Classe que verifica existencia dos utilizadores e que trata da
 * sua autenticaçao
 * 
 * @author António, José e Pedro
 *
 */
public class Authentication implements AuthenticationInterface {

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
	 * existir verifica se a password está correcta
	 * 
	 * @param username
	 *            Nome do utilizador a autenticar
	 * @param password
	 *            Palavra passe do utilizador
	 * @return False caso a password esteja errada
	 * @requires username != null && password != null
	 */
	@Override
	public boolean authenticateUser(String username, String password) {
		String[] userPasswordAndSalt = userService.getUserPasswordAndSalt(username);

		// user nao existe
		if (userPasswordAndSalt == null) {
			userService.addUser(username, password);
		}
		// user existe e a password eh invalida
		else {
			byte[] passwordHash = Security.getHash((password + userPasswordAndSalt[0]).getBytes());
			return userPasswordAndSalt[1].equals(passwordHash.toString());
		}

		return false;
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
	@Override
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
	@Override
	public void addUser(String username, String password) {
		if (userService.getUserPasswordAndSalt(username) == null)
			userService.addUser(username, password);
	}

}
