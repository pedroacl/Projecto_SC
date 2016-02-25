package domain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import network.ClientMessage;
import network.ServerSocketNetwork;

public class ServerThreadContext {
	
	private Authentication authentication;
	
	private ClientMessage clientMessage;
	
	private ServerSocketNetwork serverSocketNetwork;

	
	public ServerThreadContext(Authentication authentication, ServerSocketNetwork serverSocketNetwork, 
			ClientMessage clientMessage) {
		
		this.authentication = authentication;
		this.clientMessage = clientMessage;
		this.serverSocketNetwork = serverSocketNetwork;
	}
}
