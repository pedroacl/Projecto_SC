package network;

import java.io.IOException;
import java.net.Socket;

public class ClientNetworkManager extends NetworkManager {

	public ClientNetworkManager(Socket socket) {
		super(socket);
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	@Override
	public boolean sendFile(NetworkMessage message) {
		boolean isValid = false;

		send(message);
		ServerMessage sm = (ServerMessage) receiveMessage();

		System.out.println("[ClientNetwork]: Mensagem é: " + sm.getMessageType());

		if (sm.getMessageType().equals(MessageType.OK)) {
			isValid = true;
			try {
				sendByteFile(message.getContent(), message.getFileSize());
			} catch (IOException e) {
				e.printStackTrace();
				isValid = false;
			}

		} else
			isValid = false;

		return isValid;
	}
}
