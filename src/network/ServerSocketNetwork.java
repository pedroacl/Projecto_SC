package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.Socket;

public class ServerSocketNetwork {
	
	private Socket socket;
	
	private ObjectInputStream in;
	
	private ObjectOutputStream out;


	public ServerSocketNetwork(Socket socket) {
		this.socket = socket;
		
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void close(){
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public ClientMessage getClientMessage() {
		ClientMessage clientMessage = null;
		
		try {		
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
			out.writeObject(serverMessage);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
