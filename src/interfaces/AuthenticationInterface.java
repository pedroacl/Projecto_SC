package interfaces;

import java.security.KeyPair;
import java.security.PrivateKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public interface AuthenticationInterface {

	public boolean authenticateUser(String username, String password);

	public boolean existsUser(String username);

	public void addUser(String username, String password);
	
}
