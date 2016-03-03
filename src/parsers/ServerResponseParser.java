package parsers;

import java.io.File;
import java.io.IOException;

import network.ClientNetwork;
import network.ServerMessage;
import domain.UserInterface;

public class ServerResponseParser {
	
	
	private UserInterface userInterface;
	private ClientNetwork clientNetwork;
	

	public ServerResponseParser (UserInterface userInterface, ClientNetwork clientNetwork) {
		this.userInterface = userInterface;
		this.clientNetwork = clientNetwork;
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
			//userInterface.printConversation(serverMessage.getList());
			break;
			
		case FILE:
			try {
				File file = clientNetwork.receiveFile(serverMessage.getFileSize(), serverMessage.getMessage());
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
