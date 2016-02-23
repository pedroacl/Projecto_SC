package entities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import Request;

public class Server {
	private static final int serverPort = 8080;	
	
	public static void main(String[] args) {
		
		ArrayList<User> users = loadUsers();
		
		ServerSocket serverSocket = null;
		
		//criar socket
		try {		
			serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		Socket socket = null;
		
		//aceitar pedidos
		try {			
			socket = serverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ObjectInputStream in = null;
				
		try {
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		ObjectOutputStream out = null;
		
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Request clientRequest = null;
		//out.writeObject(answer);
		
		//aceitar pedidos
		while(true) {
			try {
				clientRequest = (Request) in.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			ServerThread serverThread = new ServerThread(clientRequest);
			serverThread.run();
		}
		
		//serverSocket.close();
		//socket.close();
	}
	
	private static ArrayList<User> loadUsers() {
		return new ArrayList<User>();
	}
}
