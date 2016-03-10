package domain;
import java.net.Socket;

import network.ServerNetwork;
import network.ServerSocketNetwork;

public class Server {
	
	private static Authentication authentication;	
	
	private static ServerNetwork serverNetwork;
	
	//TODO receber IP por argumentos

	public static void main(String[] args) {
		String portString = args[0];
		int serverPort = Integer.parseInt(portString);
		
		authentication = Authentication.getInstance();
		
		serverNetwork = new ServerNetwork(serverPort);
		System.out.println("Servidor inicializado e ah espera de pedidos.");

		while(true) {
			Socket socket = serverNetwork.getRequest();
			System.out.println("Cliente ligado!");

			ServerSocketNetwork serverSocketNetwork = new ServerSocketNetwork(socket);
			ServerThreadContext serverThreadContext = new ServerThreadContext(authentication, serverSocketNetwork);

			ServerThread serverThread = new ServerThread(serverThreadContext);
			serverThread.run();
			
			serverSocketNetwork.close();
		}																																																																	
		
		//serverNetwork.disconnect();
	}
}
