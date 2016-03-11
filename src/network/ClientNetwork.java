package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientNetwork {

	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String serverIP;
	private int serverPort;

	public ClientNetwork(String serverIP, String serverPort) {
		this.serverPort = Integer.parseInt(serverPort);
		this.serverIP = serverIP;
	}

	public boolean connect() {
		socket = getSocket();

		try {
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (socket != null && in != null && out != null) {
			return true;
		}

		return false;
	}

	public void disconnetc() {
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Socket getSocket() {
		Socket socket;

		for (int i = 0; i < 50; i++) {
			try {
				socket = new Socket(serverIP, serverPort);
				return socket;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// tentar proximo porto
				System.out.println("Porto " + serverPort + " nao disponivel");
				serverPort++;
				continue;
			}
		}

		return null;
	}

	private void send(ClientMessage message) {
		try {
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param message
	 */
	public void sendMessage(ClientMessage message) {
		if (message.getMessageType().equals(MessageType.FILE))
			sendFile(message);
		else
			send(message);
	}

	/**
	 * 
	 * @return
	 */
	public ServerMessage receiveMessage() {
		ServerMessage serverMessage = null;

		try {
			serverMessage = (ServerMessage) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return serverMessage;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public boolean sendFile(ClientMessage message) {
		boolean isValid = false;

		send(message);
		ServerMessage sm = receiveMessage();

		System.out.println("[ClientNetwork]: Mensagem é: " + sm.getMessageType());

		if (sm.getMessageType().equals(MessageType.OK)) {
			isValid = true;
			try {
				sendByteFile(message.getMessage(), message.getFileSize());
			} catch (IOException e) {
				e.printStackTrace();
				isValid = false;
			}

		} else
			isValid = false;

		return isValid;
	}

	/**
	 * 
	 * @param name
	 * @param fileSize
	 * @throws IOException
	 */
	private void sendByteFile(String name, int fileSize) throws IOException {
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
}
