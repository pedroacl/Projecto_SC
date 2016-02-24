package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerNetwork {
	
	private static final int serverPort = 23456;
	private ServerSocket serverSocket;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;


	public ServerNetwork() {
		//criar socket
		try {		
			serverSocket = getServerSocket(serverPort);
			
			//aceitar pedidos
			socket = serverSocket.accept();			
			
			in = new ObjectInputStream(socket.getInputStream());					
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (BindException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
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
		
		System.out.println("Servidor ligado ao porto " + serverPort);		
		return serverSocket;
	} 
	
	
	public ClientMessage getClientMessage() {
		return null;
	}
	
}
