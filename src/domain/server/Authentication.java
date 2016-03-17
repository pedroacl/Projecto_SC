package domain.server;

import interfaces.AuthenticationInterface;
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
	 * Obtem uma instancia desta classe
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
	 *            nome do utilizador a autenticar
	 * @param password
	 *            palavra passe do utilizador
	 * @return False caso a password esteja errada
	 */
	@Override
	public boolean authenticateUser(String username, String password) {

		String userPassword = userService.getUserPassword(username);

		// user nao existe
		if (userPassword == null) {
			userService.addUser(username, password);
		}
		// user existe e a password eh invalida
		else if (!userPassword.equals(password)) {
			return false;
		}

		return true;
	}

	/**
	 * Verifica se existe um utilizador
	 * 
	 * @param username-
	 *            nome do utilizador a verificar da sua existencia
	 * 
	 * @param username
	 *            - nome do utilizador a verificar da sua existencia
	 * @return True caso utilizador exista, false caso contrario
	 */
	@Override
	public boolean existsUser(String username) {
		return userService.getUserPassword(username) != null;
	}

	/**
	 * Adiciona um utilizador ao sistema
	 * 
	 * @param username-utilizador
	 *            a ser adicionado
	 * @param password-
	 *            palavra passe do utilizador
	 */
	@Override
	public void addUser(String username, String password) {
		if (userService.getUserPassword(username) == null)
			userService.addUser(username, password);
	}
}
