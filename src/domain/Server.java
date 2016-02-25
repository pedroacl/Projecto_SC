package domain;
import network.ClientMessage;
import network.ServerNetwork;

public class Server {
	
	private static Authentication authentication;	
	
	private static ServerNetwork serverNetwork;
	
	
	public static void main(String[] args) {
		
		authentication = new Authentication();
		
		ClientMessage clientRequest = null;
		
		//aceitar pedidos
		System.out.println("Servidor inicializado e ah espera de pedidos.");

		while(true) {
			clientRequest = serverNetwork.getClientMessage();
			
			System.out.println("Mensagem recebida!");
			System.out.println(clientRequest);
			
			ServerThread serverThread = new ServerThread(authentication, clientRequest);
			serverThread.run();
		}																																																																	
		
		//serverSocket.close();
		//socket.close();
	}

}
