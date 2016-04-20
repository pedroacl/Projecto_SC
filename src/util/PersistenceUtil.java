package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe auxiliar que disponibiliza diversas funções para facilitar a
 * persistência de dados
 *
 */
public class PersistenceUtil {
	private static final String KEYSTORE_PATH = "keystore.cliente";

	/**
	 * Elimina um determinado ficheiro ou pasta
	 * 
	 * @param file
	 *            Caminho relativo do ficheiro ou pasta a ser eliminado
	 */
	public static void delete(File file) {
		// file corresponde a uma directoria
		if (file.isDirectory()) {
			// obter todos os ficheiros presentes na directoria
			File[] files = file.listFiles();

			// directoria contem ficheiros
			// eliminar cada ficheiro presente na directoria
			for (File c : files)
				delete(c);
		}

		// eliminar ficheiro ou pasta vazia
		file.delete();
	}

	/**
	 * Cria um ficheiro e todas as subdirectorias necessárias caso este não
	 * exista já
	 * 
	 * @param filePath
	 *            Caminho relativo do ficheiro
	 */
	public static void createFile(String filePath) {
		File file = new File(filePath);

		// ficheiro nao existe
		if (!file.exists()) {
			if (file.getParentFile() != null)
				// criar subdirectorias do ficheiro
				file.getParentFile().mkdirs();

			try {
				// criar ficheiro
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Cria uma directoria e todas as subdirectorias necessárias
	 * 
	 * @param path
	 *            Caminho relativo da directoria a ser criada
	 */
	public static void createDir(String path) {
		File file = new File(path);

		// ficheiro nao existe
		if (!file.exists())
			file.mkdirs();
	}

	/**
	 * Persiste um determinado objecto para um ficheiro
	 * 
	 * @param obj
	 *            Objecto a ser persistido
	 * @param filePath
	 *            Directoria do ficheiro a ser persistido
	 */
	public static void writeObject(Object obj, String filePath) {
		File file = new File(filePath);
		
		try {
			// preparar streams
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

			// persistir objecto
			objectOutputStream.writeObject(obj);

			// fechar streams
			objectOutputStream.close();
			fileOutputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lê um determinado objecto de um ficheiro
	 * 
	 * @param filePath
	 *            Directoria do ficheiro a ser lido
	 * @return Devolve o objecto lido do ficheiro
	 */
	public static Object readObject(String filePath) {
		File file = new File(filePath);

		// ficheiro nao existe ou estah vazio
		if (!file.exists() || file.length() == 0)
			return null;

		Object obj = null;

		try {
			// preparar streams
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

			// ler objecto
			obj = objectInputStream.readObject();

			// fechar streams
			objectInputStream.close();
			fileInputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return obj;
	}

	/**
	 * Persiste uma string para um ficheiro de texto
	 * 
	 * @param content
	 *            String a ser persistida
	 * @param path
	 *            Directoria da string a ser persistida
	 */
	public static void writeStringToFile(String content, String filePath) {

		FileWriter fileWriter;
		
		File file = new File(filePath);
		
		if (!file.exists()) {
			PersistenceUtil.createFile(filePath);
		}
		
		try {
			fileWriter = new FileWriter(filePath, false);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Obtem o conteúdo de um ficheiro de texto
	 * 
	 * @param path
	 *            Caminho relativo para o ficheiro
	 * @return Devolve uma lista de linhas
	 */
	public static List<String> readFromFile(String path) {
		ArrayList<String> txt = new ArrayList<String>();

		try {
			BufferedReader inF = new BufferedReader(new FileReader(path));
			String reader;

			// ler cada linha do ficheiro
			while ((reader = inF.readLine()) != null)
				// adicionar linha à lista
				txt.add(reader);

			inF.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return txt;
	}

	/**
	 * 
	 * @return
	 */
	public static byte[] readBytesFromFile(String filePath) {
		File file = new File(filePath);
		byte[] input = new byte[(int) file.length()];

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(input);
			fileInputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}

	/**
	 * Obter keystore guardada num ficheiro
	 * 
	 * @param userPassword
	 *            Password de acesso à keystore
	 * @throws IOException
	 */
	public static KeyStore getKeyStore(String keystorePassword) throws IOException {
		FileInputStream fileInputStream = null;
		KeyStore keyStore = null;

		try {
			fileInputStream = new FileInputStream(KEYSTORE_PATH);
			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(fileInputStream, keystorePassword.toCharArray());
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

}
