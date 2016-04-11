package security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import util.PersistenceUtil;

public class Security {
	private static final String KEYSTORE_PASSWORD = "1234";
	
	public static byte[] getHash(byte[] message) {
		MessageDigest messageDigest = null;
		
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return messageDigest.digest(message);
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
	 * @param username 
	 * 
	 * @param secretKey
	 * @return Devolve a chave privada cifrada
	 */
	public static byte[] wrapSecretKey(String username, SecretKey secretKey) {
		String password = "Come you spirits that tend on mortal thoughts";
		byte[] salt = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99, (byte) 0x52, (byte) 0x3e, (byte) 0xea, (byte) 0xf2 };

		PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray( ));		
		byte[] wrappedKey = null;
		
		try {
			SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey passwordKey = kf.generateSecret(keySpec);
			PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 20);
			Cipher c = Cipher.getInstance("PBEWithMD5AndAES");
			c.init(Cipher.WRAP_MODE, passwordKey, paramSpec);
			
			// cifrar a chave secreta que queremos enviar
			wrappedKey = c.wrap(secretKey);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		
		return wrappedKey;
	}
	
	/**
	 * Obtem uma chave publica guardada na keystore
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
}
