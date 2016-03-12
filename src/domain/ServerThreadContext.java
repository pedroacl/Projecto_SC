package domain;

import network.ServerSocketNetwork;

/**
 * 
 * @author Pedro, Jose, Antonio
 *
 */
public class ServerThreadContext {

	private ServerSocketNetwork serverSocketNetwork;

	public ServerThreadContext(Authentication authentication, ServerSocketNetwork serverSocketNetwork) {
		this.serverSocketNetwork = serverSocketNetwork;
	}

	public ServerSocketNetwork getServerSocketNetwork() {
		return serverSocketNetwork;
	}

	public void setServerSocketNetwork(ServerSocketNetwork serverSocketNetwork) {
		this.serverSocketNetwork = serverSocketNetwork;
	}
}
