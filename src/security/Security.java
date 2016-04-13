package security;

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
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import util.PersistenceUtil;

public class Security {
	private static final String KEYSTORE_PASSWORD = "1234";

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

	public static SecretKey getSecretKey() {
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

	public void signFile() {
		// TODO Auto-generated method stub
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
	 * 
	 * @param secretKey
	 * @return Devolve a chave privada cifrada
	 */
	public static byte[] wrapSecretKey(String username, SecretKey secretKey) {
		byte[] wrappedKey = null;

		try {
			// obter certificado
			KeyStore keystore = PersistenceUtil.getKeyStore(KEYSTORE_PASSWORD);
			Certificate certificate = keystore.getCertificate(username);

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
		} catch (KeyStoreException e) {
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
	public PublicKey getPublicKey(String username) {
		PublicKey publicKey = null;

		try {
			KeyStore keyStore = PersistenceUtil.getKeyStore(KEYSTORE_PASSWORD);
			Certificate certificate = keyStore.getCertificate(username);
			publicKey = certificate.getPublicKey();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		return publicKey;
	}

	/**
	 * 
	 * @return
	 */
	public static int generateSalt() {
		final SecureRandom randomNumber = new SecureRandom();
		return (randomNumber.nextInt(900000) + 100000);
	}
}
