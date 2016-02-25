package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerNetwork {
	
	private ServerSocket serverSocket;

	public ServerNetwork(int serverPort) {
		serverSocket = getServerSocket(serverPort);
	}

	
	private static ServerSocket getServerSocket(int serverPort) {
		ServerSocket serverSocket = null;
	
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
	
	
	public ClientMessage getClientMessage() {
		Socket socket;
		ObjectInputStream in;
		ObjectOutputStream out;
		
		try {		
			//aceitar pedido
			socket = serverSocket.accept();
			
			in = new ObjectInputStream(socket.getInputStream());					
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (BindException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		return null;
	}
}
