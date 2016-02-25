package domain;

import network.ClientMessage;
import network.ServerSocketNetwork;

public class ServerThreadContext {
	
	private Authentication authentication;
	
	private ServerSocketNetwork serverSocketNetwork;
	

	public ServerThreadContext(Authentication authentication, ServerSocketNetwork serverSocketNetwork) {
		
		this.authentication = authentication;
		this.serverSocketNetwork = serverSocketNetwork;
	}
	
	
	public ServerSocketNetwork getServerSocketNetwork() {
		return serverSocketNetwork;
	}


	public void setServerSocketNetwork(ServerSocketNetwork serverSocketNetwork) {
		this.serverSocketNetwork = serverSocketNetwork;
	}
}
