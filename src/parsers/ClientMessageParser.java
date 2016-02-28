package parsers;


import java.io.File;

import domain.Authentication;
import entities.ChatMessage;
import network.ClientMessage;
import network.MessageType;
import network.ServerMessage;
import network.ServerSocketNetwork;

public class ClientMessageParser {
	
	private ClientMessage clientMessage;

	private Authentication authentication;

	private ServerSocketNetwork ssn;
	

	public ClientMessageParser(ClientMessage clientMessage, Authentication auth) {
		this.clientMessage = clientMessage;
		authentication = auth;
	}
	
	public ServerMessage processRequest() {
		ServerMessage serverMessage = null;
		
		switch (clientMessage.getMessageType().toString()) {
		
		case "MESSAGE":
			authentication.authenticateUser(clientMessage.getUsername(), clientMessage.getPassword());

			if(authentication.exists(clientMessage.getDestination())) {
				ChatMessage chatMessage = new ChatMessage(clientMessage.getUsername(),
					clientMessage.getDestination(), 
					clientMessage.getMessage(), 
					MessageType.MESSAGE);

				//conversationDAO.addChatMessage(chatMessage);
				serverMessage = new ServerMessage(MessageType.OK);
			}
			else {
				serverMessage = new ServerMessage(MessageType.NOK);
				serverMessage.setContent("Não existe esse contact");
			}
				
			break;
			
		case "FILE":
			authentication.authenticateUser(clientMessage.getUsername(), clientMessage.getPassword());

			if(authentication.exists(clientMessage.getDestination()) && 
					clientMessage.getFileSize() < Integer.MAX_VALUE) {

				String path = fileDAO.saveFile(clientMessage.getUsername(),
						clientMessage.getDestination(), clientMessage.getMessage());
				
				File file = ssn.receiveFile(clientMessage.getFileSize(), path);
				
				ChatMessage chatMessage = new ChatMessage(clientMessage.getUsername()
						,clientMessage.getDestination(), clientMessage.getMessage(), MessageType.File);

				//ConversationDAO.addChatMessage(???,chatMessage);
				serverMessage = new ServerMessage(MessageType.OK);
			}
			else {
				serverMessage = new ServerMessage(MessageType.NOK);
				serverMessage.setContent("Não existe esse contact");
			}
			
			break;

		default:
			break;
		}
	}
}
