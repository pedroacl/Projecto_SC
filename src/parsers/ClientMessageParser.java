package parsers;

import java.util.HashMap;

import entities.ChatMessage;
import network.ClientMessage;
import network.MessageType;

public class ClientMessageParser {
	
	private ClientMessage clientMessage;
	
	private HashMap<String, String> users;

	public ClientMessageParser(ClientMessage clientMessage) {
		this.clientMessage = clientMessage;
		users = new HashMap<String, String>();
	}
	
	public void processRequest() {
		
		switch (clientMessage.getMessageType().toString()) {
		case "MESSAGE":
			ChatMessage chatMessage = new ChatMessage(clientMessage.getUsername()
					,clientMessage.getDestination(), MessageType.MESSAGE);
			ProcessRequest process
			
			
			break;

		default:
			break;
		}
	}
}
