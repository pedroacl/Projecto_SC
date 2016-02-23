package Domain;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import entities.User;

public class Server {
	private static final int serverPort = 8080;
	
	private ArrayList<User> users;
	
	private Authentication authentication;	
	
	public static void main(String[] args) {
		ArrayList<User> users = loadUsers();
		
		Authentication authentication = new Authentication(users);
		
		ServerSocket serverSocket = null;
		Socket socket = null;
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		
		//criar socket
		try {		
			serverSocket = new ServerSocket(serverPort);
			
			//aceitar pedidos
			socket = serverSocket.accept();			
			
			in = new ObjectInputStream(socket.getInputStream());					
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
				ServerThread serverThread = new ServerThread(authentication, clientRequest);
				serverThread.run();

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//serverSocket.close();
		//socket.close();
	}
	
	
	private static ArrayList<User> loadUsers() {
		FileInputStream in;
		ObjectInputStream out;
		ArrayList<User> users = null;
		
		try {
			in = new FileInputStream("users");
			out = new ObjectInputStream(in);
			users = (ArrayList<User>) out.readObject();
									
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		return users;
	}
}
