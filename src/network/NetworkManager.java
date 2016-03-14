package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class NetworkManager {

	private Socket socket;

	private ObjectInputStream in;

	private ObjectOutputStream out;

	public NetworkManager() {

	}

	/**
	 * 
	 * @param socket
	 */
	public NetworkManager(Socket socket) {
		this.socket = socket;
		
		System.out.println(socket == null);

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
	 * @return
	 */
	public Object receiveMessage() {
		Object networkMessage = null;

		try {
			networkMessage = in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return networkMessage;
	}

	/**
	 * 
	 * @param sizeFile
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public File receiveFile(int sizeFile, String name) throws IOException {

		System.out.println("[ClientNetwork] O nome do ficheiro é: " + name);
		File file = new File(name);
		FileOutputStream fileOut = new FileOutputStream(file);

		System.out.println("[ClientNetwork] O tamnho do ficheiro é :" + sizeFile);
		System.out.println(file.getAbsolutePath());

		int packageSize = 1024;
		int currentLength = 0;
		byte[] bfile = new byte[packageSize];
		int lido;

		while (currentLength < sizeFile) {
			int resto = sizeFile - currentLength;
			int numThisTime = resto < packageSize ? resto : bfile.length;
			lido = in.read(bfile, 0, numThisTime);

			if (lido == -1) {
				break;
			}

			fileOut.write(bfile, 0, numThisTime);
			currentLength += lido;
		}

		System.out.println("[ClientNetwork] li no total2: " + currentLength);
		fileOut.close();

		return file;

	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	protected boolean send(Object message) {
		boolean sent = true;
		try {
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
			sent = false;
		}

		return sent;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public abstract boolean sendFile(NetworkMessage message);

	/**
	 * 
	 * @param message
	 */
	public boolean sendMessage(NetworkMessage message) {

		System.out.println("..." + (message.getMessageType()));
		
		if (message.getMessageType().equals(MessageType.FILE))
			return sendFile(message);
		else
			return send(message);
	}

	/**
	 * 
	 * @param name
	 * @param fileSize
	 * @throws IOException
	 */
	protected void sendByteFile(String name, int fileSize) throws IOException {
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
			System.out.println("li: " + lido);
			currentLength += lido;
			out.write(bfile, 0, bfile.length);
		}

		out.flush();
		fileInputStream.close();
	}
}
