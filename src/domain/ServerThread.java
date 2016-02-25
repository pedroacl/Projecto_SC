package domain;

import interfaces.ServerThreadInterface;
import network.ClientMessage;
import network.ServerMessage;
import network.ServerSocketNetwork;

public class ServerThread extends Thread implements ServerThreadInterface {
	
	private ServerThreadContext serverThreadContext;
	
	
	public ServerThread(ServerThreadContext serverThreadContext) {
		this.serverThreadContext = serverThreadContext;
	}

	
	public void run() {
		ServerSocketNetwork serverSocketNetwork = serverThreadContext.getServerSocketNetwork();
		ClientMessage clientMessage = serverSocketNetwork.getClientMessage();
		System.out.println("Mensagem: " + clientMessage);
		System.out.println("Thread terminada.");
	}

	
	public ServerMessage processRequest(ClientMessage clientRequest) {
		
		ServerMessage serverResponse = new ServerMessage();
		
		//String user = clientRequest.getFromUser();
		String password = clientRequest.getPassword();
		
		//authentication.authenticateUser(user, password);
		
		switch (clientRequest.getMessageType()) {
		//adicionar utilizador
		case ADDUSER:	
			//serverResponse.setMessage("Teste");
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
