package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import exceptions.InvalidMacException;

public class SecurityUtils {
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

	public static byte[] signFile(String message, String username) {
		// TODO

		return null;
	}

	/**
	 * Verifica a assinatura digital de uma mensagem
	 * 
	 * @param message
	 * @param certificate
	 * @param signature
	 * @return Devolve true caso a assinatura seja válida e false caso contrário
	 */
	public static boolean verifySignature(String message, Certificate certificate, byte[] signature) {
		PublicKey publicKey = certificate.getPublicKey();

		try {
			Signature sign = Signature.getInstance("MD5withRSA");
			sign.initVerify(publicKey);
			sign.update(message.getBytes());

			return sign.verify(signature);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 
	 * @param username
	 * @param userPassword
	 * @param wrappedSessionKey
	 * @param cipheredMessage
	 * @return
	 */
	public static String decipherChatMessage(String username, String userPassword, byte[] wrappedSessionKey,
			byte[] cipheredMessage) {

		String decipheredChatMessage = null;

		try {
			SecretKey sessionKey = unwrapSessionKey(username, userPassword, wrappedSessionKey);
			System.out.println("[SecurityUtils.decipherChatMessage] SecretKey"
					+ Base64.getEncoder().encodeToString(sessionKey.getEncoded()));

			byte[] decipheredChatMessageBytes = decipherWithSessionKey(cipheredMessage, sessionKey);
			decipheredChatMessage = new String(decipheredChatMessageBytes, StandardCharsets.UTF_8);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return decipheredChatMessage;
	}

	/**
	 * 
	 * @param message
	 * @param secretKey
	 * @return
	 */
	public static byte[] cipherWithSessionKey(byte[] message, SecretKey secretKey) {
		byte[] encryptedMessage = null;

		printSecretKey(secretKey);

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

	private static byte[] decipherWithSessionKey(byte[] cipheredMessage, SecretKey secretKey) {
		byte[] decipheredMessage = null;

		try {
			Cipher cipher = Cipher.getInstance("AES");

			byte[] keyEncoded = secretKey.getEncoded();
			SecretKeySpec keySpec = new SecretKeySpec(keyEncoded, "AES");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);

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

		return decipheredMessage;
	}

	/**
	 * Função que cifra a chave privada a ser enviada
	 * 
	 * @param alias
	 *            Nome do dono da chave publica a ser usada para cifrar
	 * 
	 * @param secretKey
	 * @return Devolve a chave privada cifrada
	 */
	public static byte[] wrapSecretKey(String username, String alias, String userPassword, SecretKey secretKey) {
		byte[] wrappedKey = null;

		try {
			// obter certificado
			Certificate certificate = SecurityUtils.getCertificate(username, alias, userPassword);

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
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return wrappedKey;
	}

	public static SecretKey unwrapSessionKey(String username, String userPassword, byte[] wrappedKey)
			throws IOException {
		Key unwrappedSecretKey = null;

		try {
			// obter keystore
			KeyStore keystore = getKeyStore(username, userPassword);
			Key privateKey = (PrivateKey) keystore.getKey(username, userPassword.toCharArray());

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
	 * @throws KeyStoreException
	 * @throws IOException
	 */
	public static Certificate getCertificate(String username, String alias, String userPassword)
			throws KeyStoreException, IOException {
		Certificate certificate = null;

		KeyStore keyStore = getKeyStore(username, userPassword);
		certificate = keyStore.getCertificate(alias);

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
	 * @param usersFilePath
	 * @param serverPassword
	 * @return
	 * @throws InvalidMacException
	 */
	public static void validateFileMac(String filePath, String serverPassword) throws InvalidMacException {
		System.out.println("[Authentication.validateUsersFileMac] filePath: " + filePath);

		try {
			File usersFileMacPath = new File(filePath + ".mac");

			// nao existe ficheiro MAC
			if (!usersFileMacPath.exists()) {
				System.out.println("Ficheiro MAC não existe");
				SecurityUtils.updateFileMac(filePath, serverPassword);

			} else {
				System.out.println("[Authentication.validateUsersFileMac] ficheiro MAC existe");

				// obter MAC original
				BufferedReader inF = new BufferedReader(new FileReader(usersFileMacPath));
				String originalMAC = inF.readLine();
				inF.close();

				// gerar MAC atual
				System.out.println(filePath);
				System.out.println(serverPassword);
				String currentMacString = MiscUtil.bytesToHex(SecurityUtils.generateFileMac(filePath, serverPassword));

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
	 * 
	 * @param secretKey
	 */
	public static byte[] generateFileMac(String filePath, String serverPassword) {
		byte[] digest = null;

		File file = new File(filePath);

		if (!file.exists()) {
			System.out.println("[generateFileMac] Ficheiro nao existe (" + filePath + ")");
			return null;
		}

		try {
			// byte[] salt = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte)
			// 0x99, (byte) 0x52, (byte) 0x3e, (byte) 0xea,
			// (byte) 0xf2 };

			// PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 20);

			PBEKeySpec keySpec = new PBEKeySpec(serverPassword.toCharArray());

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

			bufferedReader.close();
			digest = mac.doFinal();

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

	/**
	 * Atualiza o código MAC presente no ficheiro ficheiro de utilizadores
	 * 
	 * @param filePath
	 *            Localização do ficheiro de utilizadores
	 * @param serverPassword
	 *            Password do servidor utilizada para gerar o MAC
	 */
	public static void updateFileMac(String filePath, String serverPassword) {
		System.out.println("[SecurityUtils.updateFileMac] filePath: " + filePath);

		File macFilePath = new File(filePath + ".mac");

		if (!macFilePath.exists()) {
			System.out.println("[SecurityUtils.updateFileMac] Nao existe MAC file");
			createMacFile(filePath, serverPassword);
		}

		try {
			// abrir ficheiro em modo overwrite
			FileWriter fileWriter = new FileWriter(macFilePath, false);

			// obter novo MAC
			byte[] fileMac = SecurityUtils.generateFileMac(filePath, serverPassword);

			// guardar novo MAC
			fileWriter.write(MiscUtil.bytesToHex(fileMac));

			fileWriter.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cria um ficheiro que irá guardar o MAC do ficheiro de utilizadores
	 * 
	 * @param usersFilePath
	 *            Localização do ficheiro de utilizadores
	 * @param serverPassword
	 *            Password do servidor usada para gerar o MAC
	 * @return Devolve true caso o ficheiro tenha sido criado e false caso já
	 *         exista
	 */
	public static boolean createMacFile(String filePath, String serverPassword) {
		System.out.println("[SecurityUtils.creatMacFile] " + filePath);
		File file = new File(filePath + ".mac");

		if (file.exists()) {
			return false;
		}

		try {
			// abrir ficheiro em modo overwrite
			FileWriter fileWriter = new FileWriter(file, false);

			// obter mac do ficheiro de utilizadores
			byte[] fileMac = generateFileMac(filePath, serverPassword);

			System.out.println("!!!!" + fileMac == null);

			// guardar mac
			fileWriter.write(MiscUtil.bytesToHex(fileMac));
			fileWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * Obter keystore guardada num ficheiro
	 * 
	 * @param userPassword
	 *            Password de acesso à keystore
	 * @throws IOException
	 */
	private static KeyStore getKeyStore(String username, String userPassword) throws IOException {
		KeyStore keyStore = null;

		System.out.println("[SecurityUtils.getKeystore] username: " + username);
		System.out.println("[SecurityUtils.getKeystore] userPassword: " + userPassword);

		try {
			FileInputStream fileInputStream = new FileInputStream("keystore." + username);
			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(fileInputStream, userPassword.toCharArray());
			fileInputStream.close();

		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}

		return keyStore;
	}

	public static void printSecretKey(SecretKey key) {
		byte[] chave = key.getEncoded();

		StringBuilder sb = new StringBuilder("------------------> ");

		for (int i = 0; i < chave.length; i++) {

			sb.append(chave[i]);
		}

		System.out.println(sb.toString());

	}
}
