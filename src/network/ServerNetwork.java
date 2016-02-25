package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerNetwork {
	
	private ServerSocket serverSocket;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public ServerNetwork(int serverPort) {
		serverSocket = getServerSocket(serverPort);

		try {
			socket = serverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private ServerSocket getServerSocket(int serverPort) {
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

	
	public void disconnect (){
		
		
	}
	
	public ClientMessage getClientMessage() {
		ClientMessage clientMessage = null;
			
		try {		
			in = new ObjectInputStream(socket.getInputStream());					
			
	 		clientMessage = (ClientMessage) in.readObject();
			in.close();
			
		} catch (BindException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return clientMessage;
	}
	
	
	public void sendMessage(ServerMessage serverMessage) {
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(serverMessage);

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
