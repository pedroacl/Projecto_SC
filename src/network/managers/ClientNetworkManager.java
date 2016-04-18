package network.managers;

import java.io.IOException;
import java.net.Socket;

import javax.crypto.SecretKey;

import network.messages.MessageType;
import network.messages.NetworkMessage;
import network.messages.ServerMessage;

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
	public boolean sendFile(NetworkMessage message, SecretKey key) {
		boolean isValid = false;

		ServerMessage sm = (ServerMessage) receiveMessage();

		System.out.println("[ClientNetwork]: Mensagem é: " + sm.getMessageType());

		if (sm.getMessageType().equals(MessageType.OK)) {
			isValid = true;
			try {
				sendByteFile(message.getContent(), message.getFileSize(), key);
			} catch (IOException e) {
				e.printStackTrace();
				isValid = false;
			}

		} else
			isValid = false;

		return isValid;
	}
}
