package parsers;

import java.util.HashMap;

import network.ClientMessage;

public class ClientMessageParser {
	
	private ClientMessage clientMessage;
	
	private HashMap<String, String> users;

	public ClientMessageParser(ClientMessage clientMessage) {
		this.clientMessage = clientMessage;
		users = new HashMap<String, String>();
	}
}
