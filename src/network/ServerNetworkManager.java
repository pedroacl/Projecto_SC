package network;

import java.io.IOException;
import java.net.Socket;

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
