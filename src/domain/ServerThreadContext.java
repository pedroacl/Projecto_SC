package domain;

import network.ClientMessage;
import network.ServerSocketNetwork;

public class ServerThreadContext {
	
	private Authentication authentication;
	
	private ClientMessage clientMessage;
	
	public ClientMessage getClientMessage() {
		return clientMessage;
	}


	public void setClientMessage(ClientMessage clientMessage) {
		this.clientMessage = clientMessage;
	}


	private ServerSocketNetwork serverSocketNetwork;

	
	public ServerThreadContext(Authentication authentication, ServerSocketNetwork serverSocketNetwork, 
			ClientMessage clientMessage) {
		
		this.authentication = authentication;
		this.clientMessage = clientMessage;
		this.serverSocketNetwork = serverSocketNetwork;
	}
}
