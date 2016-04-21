package network.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.crypto.SecretKey;

import network.messages.NetworkMessage;
import security.SecurityUtils;

public class ClientNetworkManager extends NetworkManager {

	public ClientNetworkManager(Socket socket) {
		super(socket);
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public boolean sendFile(NetworkMessage message, SecretKey key) {
		boolean isValid = true;

		try {
			sendByteFile(message.getContent(), message.getFileSize(), key);
		} catch (IOException e) {
			e.printStackTrace();
			isValid = false;
		}

		return isValid;
	}
	
	public File receiveFile(int fileSize, String name, SecretKey key) throws IOException {

		File file = new File(name);
		FileOutputStream fileOut = new FileOutputStream(file);

		int packageSize = getPackageSize();
		int currentLength = 0;
		byte[] bfile = new byte[packageSize];
		int lido;

		while (currentLength < fileSize) {
			int resto = fileSize - currentLength;
			int numThisTime = resto < packageSize ? resto : bfile.length;
			lido = in.read(bfile, 0, numThisTime);

			if (lido == -1) {
				break;
			}
			
			

			fileOut.write(bfile, 0, numThisTime);
			currentLength += lido;
		}

		fileOut.close();

		return file;
	}
	
	/**
	 * 
	 * @param name
	 * @param fileSize
	 * @throws IOException
	 */
	
	private void sendByteFile(String name, int fileSize, SecretKey key) throws IOException {
		int packageSize = PACKAGE_SIZE;

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
			
			bfile = SecurityUtils.cipherWithSessionKey(bfile, key);
			
			out.write(bfile, 0, bfile.length);
		}

		out.flush();
		fileInputStream.close();
	}

	
}
