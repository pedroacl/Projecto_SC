package Domain;

import Domain.Request;
import interfaces.ServerThreadInterface;

public class ServerThread extends Thread implements ServerThreadInterface {
	
	private Request clientRequest;
	
	public ServerThread(Request clientRequest) {
		this.clientRequest = clientRequest; 
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
			
			break;
			
		case ERR:
			
			break;
			
		case FILE:
			
			break;
			
		case MESSAGE:
			
			break;

		default:
			break;
		}
		
		
		
		return serverResponse;
	}
}
