package Domain;

import Domain.Request;
import interfaces.ServerThreadInterface;

public class ServerThread extends Thread implements ServerThreadInterface {
	
	private Request clientRequest;
	
	private Authentication authentication;
	
	public ServerThread(Authentication authentication, Request clientRequest) {
		this.clientRequest = clientRequest;
		this.authentication = authentication;
	}

	public void run() {
		
	}
	
	public Request processRequest(Request clientRequest) {
		
		Request serverResponse = new Request();
		
		switch (clientRequest.getRequestType()) {
		//adicionar utilizador
		case ADDUSER:	
			serverResponse.setMessage("Teste");
			break;
			
		//remover utilizador
		case REMOVEUSER:
			
			break;
			
		case AUTH:
			//authentication.authenticateUser(clientRequest.getFromUser(), authentication.getUsers());
			break;
			
		case ERR:
			
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
