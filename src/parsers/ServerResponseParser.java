package parsers;

import java.io.IOException;

import network.ClientNetwork;
import network.ServerMessage;
import util.UserUtil;

public class ServerResponseParser {

	private UserUtil userInterface;
	private ClientNetwork clientNetwork;
	private String username;

	public ServerResponseParser(UserUtil userInterface, ClientNetwork clientNetwork, String username) {
		this.userInterface = userInterface;
		this.clientNetwork = clientNetwork;
		this.username = username;
	}

	public void ProcessMessage(ServerMessage serverMessage) {
		switch (serverMessage.getMessageType()) {
		case OK:
			userInterface.print("OK");
			break;

		case NOK:
			userInterface.print(serverMessage.getMessage());
			break;

		case CONVERSATION:
			userInterface.printChatMessages(serverMessage.getMessageList(), username);
			break;
			
		case LAST_MESSAGES:
			userInterface.printContactChatMessages(serverMessage.getMessageList(), username);
			break;

		// receber um ficheiro
		case FILE:
			try {
				clientNetwork.receiveFile(serverMessage.getFileSize(), serverMessage.getMessage());
			} catch (IOException e) {
				userInterface.print("ERRO a receber o ficheiro");
				e.printStackTrace();
			}
			break;

		default:
			userInterface.print("Mensagem invalida");
			break;
		}
	}
}
