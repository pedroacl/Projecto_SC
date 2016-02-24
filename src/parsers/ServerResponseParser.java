package parsers;

import network.ServerMessage;

public class ServerResponseParser {
	
	private boolean isValid;
	
	private ServerMessage serverMessage;
	
	public ServerResponseParser() {
		isValid = false;
	}

	public ServerResponseParser(ServerMessage serverMessage) {
		this();
		this.serverMessage = serverMessage;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public void parseMessage() {
		switch (serverMessage.getMessageType()) {
		case OK:
			System.out.println("OK");
			break;
			
		case NOK:
			System.out.println("NOK");
			break;
			
		case CONVERSATION:
			break;
			
		case FILE:
			break;

		default:
			break;
		}
	}
}
