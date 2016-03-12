package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Classe que permite comunicar com um cliente 
 * @author
 *
 */
public class ServerSocketNetwork {

	private Socket socket;

	private ObjectInputStream in;

	private ObjectOutputStream out;

	/**
	 * 
	 * @param socket
	 */
	public ServerSocketNetwork(Socket socket) {
		this.socket = socket;

		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Função que pertmite fechar o socket do cliente atual
	 */
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return Devolve uma mensagem enviada pelo cliente
	 */
	public ClientMessage getClientMessage() {
		ClientMessage clientMessage = null;

		try {
			clientMessage = (ClientMessage) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return clientMessage;
	}

	/**
	 * Função auxiliar que envia uma mensagem a um cliente
	 * @param message
	 */
	private void send(ServerMessage message) {
		try {
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Função que envia uma mensagem ou um ficheiro a um cliente
	 * @param message
	 */
	public void sendMessage(ServerMessage message) {
		if (message.getMessageType().equals(MessageType.FILE))
			sendFile(message);
		else
			send(message);
	}

	/**
	 * Função que envia um ficheiro a um cliente
	 * @param message
	 * @return
	 */
	public boolean sendFile(ServerMessage message) {
		boolean isValid = false;

		String filePath = message.getContent();
		message.setContent(extractName(filePath));

		send(message);

		isValid = true;
		try {
			sendByteFile(filePath, message.getFileSize());
		} catch (IOException e) {
			e.printStackTrace();
			isValid = false;
		}

		return isValid;
	}

	/**
	 * Função que permite enviar um ficheiro a um cliente
	 * @param name
	 * @param fileSize
	 * @throws IOException
	 */
	private void sendByteFile(String name, int fileSize) throws IOException {

		System.out.println("[SERVERSOCKETNETWORK]- sendByteFile: File " + name);
		int packageSize = 1024;

		FileInputStream fileInputStream = new FileInputStream(name);
		int currentLength = 0;
		byte[] bfile;
		
		while (currentLength < fileSize) {
			if ((fileSize - currentLength) < packageSize)
				bfile = new byte[(fileSize - currentLength)];
			else
				bfile = new byte[packageSize];
			
			int lido = fileInputStream.read(bfile, 0, bfile.length);
			currentLength += lido;
			out.write(bfile, 0, bfile.length);
		}
		
		out.flush();
		fileInputStream.close();
	}

	/**
	 * Função que permite receber um ficheiro de um cliente
	 * @param sizeFile
	 * @param name
	 * @return
	 */
	public File receiveFile(int sizeFile, String name) {
		File file = new File(name);
		FileOutputStream fileOut = null;

		try {
			fileOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("O tamnho do ficheiro é :" + sizeFile);

		int packageSize = 1024;
		int currentLength = 0;
		byte[] bfile = new byte[packageSize];
		int lido;

		while (currentLength < sizeFile) {
			int resto = sizeFile - currentLength;
			int numThisTime = resto < packageSize ? resto : bfile.length;

			try {
				lido = in.read(bfile, 0, numThisTime);

				if (lido == -1) {
					break;
				}

				System.out.println("li: " + lido);

				fileOut.write(bfile, 0, numThisTime);
				currentLength += lido;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("li no total2: " + currentLength);

		return file;

	}

	/**
	 * Função auxiliar
	 * @param absolutePath
	 * @return
	 */
	private String extractName(String absolutePath) {
		String[] splitName = absolutePath.split("/");
		return splitName[splitName.length - 1];
	}
}
