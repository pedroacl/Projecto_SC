package network;

import java.io.IOException;
import java.net.Socket;

/**
 * Classe que permite comunicar com um cliente
 * 
 * @author
 *
 */
public class ServerSocketNetwork extends NetworkManager {

	public ServerSocketNetwork() {
		super();
	}

	/**
	 * 
	 * @param socket
	 */
	public ServerSocketNetwork(Socket socket) {
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

	@Override
	public boolean sendFile(NetworkMessage message) {
		boolean isValid = false;
	
		String filePath = message.getContent();
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
}
