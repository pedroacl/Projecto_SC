package domain;

import java.net.Socket;

import network.ServerNetwork;

public class ServerThreadContext {
	
	private Authentication authentication;
	
	private ServerNetwork serverNetwork;
	
	public ServerThreadContext(Authentication authentication, ServerNetwork serverNetwork, Socket socket) {
		this.authentication = authentication;
		this.serverNetwork = serverNetwork;
	}
}
