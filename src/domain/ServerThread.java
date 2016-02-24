package domain;

import entities.ChatMessage;
import interfaces.ServerThreadInterface;
import network.ClientMessage;
import network.ServerMessage;

public class ServerThread extends Thread implements ServerThreadInterface {
	
	private ClientMessage clientRequest;
	
	private Authentication authentication;
	
	public ServerThread(Authentication authentication, ClientMessage clientRequest) {
		this.clientRequest = clientRequest;
		this.authentication = authentication;
	}

	
	public void run() {
		System.out.println("From: " + clientRequest.getFromUser());
		System.out.println("To: " + clientRequest.getToUser());
		
		ChatMessage serverResponse = processRequest(clientRequest);
	}

	
	public ServerMessage processRequest(ClientMessage clientRequest) {
		
		ServerMessage serverResponse = new ServerMessage();
		
		String user = clientRequest.getFromUser();
		String password = clientRequest.getPassword();
		
		authentication.authenticateUser(user, password);
		
		switch (clientRequest.getMessageType()) {
		//adicionar utilizador
		case ADDUSER:	
			serverResponse.setMessage("Teste");
			break;
			
		//remover utilizador
		case REMOVEUSER:
			
			break;
			
		case FILE:
			
			break;
			
		case MESSAGE:
			//authentication.authenticateUser(clientRequest.getFromUser(), clientRequest.get());
			break;

		default:
			break;
		}		
		
		return serverResponse;
	}
}
