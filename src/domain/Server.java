package domain;
import java.net.Socket;

import network.ClientMessage;
import network.ServerNetwork;

public class Server {
	
	private static Authentication authentication;	
	
	private static ServerNetwork serverNetwork;
	
	private static final int serverPort = 23456;

	public static void main(String[] args) {
		
		authentication = new Authentication();
		
		ClientMessage clientRequest = null;
		
		serverNetwork = new ServerNetwork(serverPort);
		System.out.println("Servidor inicializado e ah espera de pedidos.");

		while(true) {
			Socket socket = serverNetwork.getRequest();
			System.out.println("Cliente ligado!");
			
			clientRequest = serverNetwork.getClientMessage(socket);
			System.out.println("Mensagem recebida!");
			System.out.println(clientRequest);
			
			ServerThreadContext serverThreadContext = new ServerThreadContext(authentication, serverNetwork, socket);
			
			ServerThread serverThread = new ServerThread(serverThreadContext, clientRequest);
			serverThread.run();
			
			serverNetwork.closeSocket(socket);
		}																																																																	
		
		//serverNetwork.disconnect();
	}

}
