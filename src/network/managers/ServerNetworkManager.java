package network.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.crypto.SecretKey;

import network.messages.NetworkMessage;
import util.MiscUtil;

public class ServerNetworkManager extends NetworkManager {

	
	public ServerNetworkManager(Socket socket) {
		super(socket);
	}
	
	/**
	 * Função auxiliar
	 * 
	 * @param absolutePath
	 * @return
	 */
	private String extractName(String absolutePath) {
		String[] splitName = absolutePath.split("/");
		return splitName[splitName.length - 1];
	}


	public boolean sendFile(NetworkMessage message) {
		boolean isValid = false;
	
		String filePath = (message.getContent());
		message.setContent(extractName(filePath));
		
		send(message);
		
		isValid = true;
		
		try {
			sendByteFile(filePath, message.getFileSize());
		} catch (IOException e) {
			isValid = false;
			e.printStackTrace();
		}
		
		return isValid;
	}
	
	
	private void sendByteFile(String name, int fileSize) throws IOException {
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
			out.write(bfile, 0, bfile.length);
		}

		out.flush();
		fileInputStream.close();
	}
	
	/**
	 * Obtem um ficheiro enviado pela rede baseado no seu nome e tamanho
	 * 
	 * @param fileSize
	 *            Tamanho do ficheiro
	 * @param name
	 *            Nome do ficheiro
	 * @return Ficheiro recebido pela rede
	 * @throws IOException
	 * @requires fileSize >= 0 && name != null
	 */
	public File receiveFile(int fileSize, String name) throws IOException {

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

}
