package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerNetwork {

	private ServerSocket serverSocket;
	
	private int serverPort;
	

	public ServerNetwork(int serverPort) {
		this.serverPort = serverPort;
		getServerSocket();
	}

	
	private ServerSocket getServerSocket() {
		serverSocket = null;
		
		//procurar portos disponiveis
		for (int i = 0; i < 50; i++) {
			try {						
				//criar socket
				serverSocket = new ServerSocket(serverPort);
				break;
										
			} catch (IOException e) {
				serverPort++;
				continue;
			}
		}
		
		System.out.println("Servidor ligado ao porto: " + serverPort);
		
		return serverSocket;
	} 
	
	
	//obter pedido de cliente
	public Socket getRequest() {
		Socket socket = null;
		
		try {
			socket = serverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return socket;
	} 
}
