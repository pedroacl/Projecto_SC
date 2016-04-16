package domain.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import interfaces.AuthenticationInterface;
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
	 * existir verifica se a password corresponde ah respectiva sintese guardada
	 * 
	 * @param username
	 *            Nome do utilizador a autenticar
	 * @param password
	 *            Palavra passe do servidor passada por argumentos na linha de comandos
	 * @return False caso a password esteja errada
	 * @requires username != null && password != null
	 */
	@Override
	public boolean authenticateUser(String username, String password) {
		// validar integridade do ficheiro MAC
		if (!validateUsersFileMAC("users.txt", password))
			return false;

		String[] userPasswordAndSalt = userService.getUserPasswordAndSalt(username);

		// user nao existe
		if (userPasswordAndSalt == null) {
			System.out.println("User nao existe. Adicionar " + username + "!");
			userService.addUser(username, password);
		}
		// user existe e a password eh invalida
		else {
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
	public boolean validateUsersFileMAC(String usersFilePath, String password) {
		// TODO testar
		byte[] originalMAC = null;
		boolean validMAC = false;

		try {
			File usersMACFile = new File(usersFilePath + ".mac");

			// nao existe ficheiro MAC
			if (!usersMACFile.exists()) {
				updateUsersFileMAC(usersFilePath, password);

			} else {
				// gerar password
				SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
				PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
				SecretKey secretKey = kf.generateSecret(keySpec);

				// obter MAC original
				BufferedReader inF = new BufferedReader(new FileReader(usersFilePath + ".mac"));
				String orignalMACString = inF.readLine();
				originalMAC = orignalMACString.getBytes();
				inF.close();

				// gerar MAC atual
				byte[] fileMAC = SecurityUtils.generateFileMAC(usersFilePath, password);
				validMAC = Arrays.equals(fileMAC, originalMAC);
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return validMAC;
	}

	/**
	 * 
	 * @param usersFilePath
	 */
	public void updateUsersFileMAC(String usersFilePath, String password) {
		// TODO testar
		
		try {
			String macFilePath = usersFilePath + ".mac";
			File usersMACFile = new File(macFilePath);
			
			// abrir ficheiro em modo overwrite
			FileWriter fileWriter = new FileWriter(usersMACFile, false);
		
			// obter novo MAC
			byte[] fileMac = SecurityUtils.generateFileMAC(macFilePath, password);
			
			// guardar novo MAC
			fileWriter.write(MiscUtil.bytesToHex(fileMac));
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
