package domain;
import java.net.Socket;

import network.ClientMessage;
import network.ServerNetwork;
import network.ServerSocketNetwork;

public class Server {
	
	private static Authentication authentication;	
	
	private static ServerNetwork serverNetwork;
	
	private static final int serverPort = 23456;

	public static void main(String[] args) {
		
		authentication = new Authentication();
		
		serverNetwork = new ServerNetwork(serverPort);
		System.out.println("Servidor inicializado e ah espera de pedidos.");

		while(true) {
			Socket socket = serverNetwork.getRequest();
			ServerSocketNetwork serverSocketNetwork = new ServerSocketNetwork(socket);
			System.out.println("Cliente ligado!");
			
			ClientMessage clientRequest = null;
			ServerThreadContext serverThreadContext = new ServerThreadContext(authentication, serverSocketNetwork, 
					clientRequest);

			ServerThread serverThread = new ServerThread(serverThreadContext);
			serverThread.run();
			
			serverSocketNetwork.close();
		}																																																																	
		
		//serverNetwork.disconnect();
	}
}
