package security;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import util.PersistenceUtil;

public class SecurityUtils {
	private static final String KEYSTORE_PASSWORD = "seguranca";

	/**
	 * Gera uma sintese SHA 256 baseada numa string message
	 * 
	 * @param message
	 *            String para a qual vai ser gerada a sintese
	 * @return Devolve um array de bytes representativo da sintese
	 */
	public static byte[] getHash(String message) {
		byte[] hashedMessage = null;

		try {
			byte[] auxMessage = message.getBytes("UTF-8");
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			hashedMessage = messageDigest.digest(auxMessage);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return hashedMessage;
	}

	/**
	 * Obtem um par chave pública / chave privada baseado no algoritmo de chave
	 * assimétrica RSA
	 * 
	 * @return Devolve o par de chaves
	 */
	public static KeyPair getKeyPair() {
		KeyPair keyPair = null;

		try {
			KeyPairGenerator keyPairGenerator;
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return keyPair;
	}

	/**
	 * Obtem uma chave secreta baseada no algoritmo de chave simétrica AES
	 * 
	 * @return
	 */
	public static SecretKey generateSecretKey() {
		SecretKey secretKey = null;

		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			secretKey = keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return secretKey;
	}

	/**
	 * Gera a assinatura de uma string através de uma chave privada e baseado
	 * nos algoritmos MD5 e RSA
	 * 
	 * @param message
	 *            String a ser assinada
	 * @param privateKey
	 *            Chave privada a ser usada para a assinatura
	 * @return Devolve um array de bytes representativo da assinatura
	 */
	public static byte[] signMessage(String message, PrivateKey privateKey) {
		byte[] signedMessage = null;

		try {
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initSign(privateKey);
			signature.update(message.getBytes());
			signedMessage = signature.sign();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}

		return signedMessage;
	}

	public static byte[] signFile(String message, PrivateKey privateKey) {
		// TODO 
		
		return null;
	}

	/**
	 * Cifra uma mensagem com uma chave privada
	 * 
	 * @param secretKey
	 * @return Devolve uma mensagem cifrada com uma chave privada
	 */
	public static byte[] cipherWithSecretKey(byte[] message, SecretKey secretKey) {
		byte[] encryptedMessage = null;

		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			encryptedMessage = cipher.doFinal(message);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return encryptedMessage;
	}

	public static byte[] decipherWithSecretKey(byte[] cipheredMessage, SecretKey secretKey) {
		byte[] decipheredMessage;

		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			decipheredMessage = cipher.doFinal(cipheredMessage);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return cipheredMessage;
	}

	public Cipher cipherWithPublicKey() {
		Cipher cipher = null;

		return cipher;
	}

	/**
	 * Função que cifra a chave privada a ser enviada
	 * 
	 * @param username
	 *            Nome do dono da chave publica a ser usada para cifrar
	 * 
	 * @param secretKey
	 * @return Devolve a chave privada cifrada
	 */
	public static byte[] wrapSecretKey(SecretKey secretKey, Certificate certificate) {
		byte[] wrappedKey = null;

		try {
			// obter certificado
			// KeyStore keystore =
			// PersistenceUtil.getKeyStore(KEYSTORE_PASSWORD);
			// Certificate certificate = keystore.getCertificate(username);

			// inicializar cifra
			Cipher c = Cipher.getInstance("RSA");
			c.init(Cipher.WRAP_MODE, certificate);

			// cifrar a chave secreta que queremos enviar
			wrappedKey = c.wrap(secretKey);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}

		return wrappedKey;
	}

	public static SecretKey unwrapSecretKey(String username, char[] keyPassword, byte[] wrappedKey) {
		Key unwrappedSecretKey = null;

		try {
			// obter keystore
			KeyStore keystore = PersistenceUtil.getKeyStore(KEYSTORE_PASSWORD);
			Key privateKey = keystore.getKey(username, keyPassword);

			// inicializar cifra
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.UNWRAP_MODE, privateKey);

			unwrappedSecretKey = cipher.unwrap(wrappedKey, "RSA", Cipher.SECRET_KEY);

		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}

		return (SecretKey) unwrappedSecretKey;
	}

	/**
	 * Obtem uma chave publica guardada na keystore
	 * 
	 * @param username
	 */
	public static Certificate getCertificate(String username) {
		Certificate certificate = null;

		try {
			KeyStore keyStore = PersistenceUtil.getKeyStore(KEYSTORE_PASSWORD);
			certificate = keyStore.getCertificate(username);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		return certificate;
	}

	/**
	 * 
	 * @return
	 */
	public static int generateSalt() {
		final SecureRandom randomNumber = new SecureRandom();
		return (randomNumber.nextInt(900000) + 100000);
	}

	/**
	 * 
	 * @param secretKey
	 */
	public static byte[] generateFileMAC(String filePath, String password) {
		byte[] digest = null;

		try {
			// byte[] salt = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99, (byte) 0x52, (byte) 0x3e, (byte) 0xea,
					// (byte) 0xf2 };

			// PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 20);
			
			PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());

			// obter chave secreta atraves da password
			SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey secretKey = kf.generateSecret(keySpec);

			// inicializar MAC
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(secretKey);

			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
			String reader;

			// ler cada linha do ficheiro
			while ((reader = bufferedReader.readLine()) != null)
				mac.update(reader.getBytes());

			mac.doFinal();
			bufferedReader.close();

		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

		return digest;
	}
}
