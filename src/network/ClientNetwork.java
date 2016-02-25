package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientNetwork {

	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String serverIP;
	private int serverPort;
	
	public ClientNetwork(String serverIP, String serverPort) {
		this.serverPort = Integer.parseInt(serverPort);
		this.serverIP = serverIP;
	}
	

	public boolean connect() {
		socket = getSocket();
		
		try {
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (socket != null && in != null && out != null) {
			return true;
		}
		
		return false;
	}
	
	
	public void disconnetc() {
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private Socket getSocket() {
		Socket socket;

		for (int i = 0; i < 50; i++) {
			try {
				socket = new Socket(serverIP, serverPort);
				return socket;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				//tentar proximo porto
				System.out.println("Porto " + serverPort + " nao disponivel");
				serverPort++;
				continue;
			}
		}
		
		return null;
	}
	
	
	public void sendMessage(ClientMessage message) {
		try {
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public ServerMessage receiveMessage() {
		ServerMessage serverMessage = null;

		try {
			serverMessage = (ServerMessage) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return serverMessage;
	}
}
