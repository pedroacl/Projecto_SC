package domain;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import entities.User;
import network.ClientMessage;

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
			serverSocket = getServerSocket(serverPort);
			
			//aceitar pedidos
			socket = serverSocket.accept();			
			
			in = new ObjectInputStream(socket.getInputStream());					
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (BindException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		ClientMessage clientRequest = null;
		//out.writeObject(answer);
		
		//aceitar pedidos
		while(true) {
			try {
				clientRequest = (ClientMessage) in.readObject();
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
		ObjectInputStream oin;
		ArrayList<User> users = null;
		
		try {
			//criar pasta para o utilizador
			File file = new File("users");
			
			if (!file.exists()) {
				System.out.println("Ficheiro nao existe");
				file.createNewFile();
				
				return null;
			}
			
			in = new FileInputStream("users");
			oin = new ObjectInputStream(in);
			users = (ArrayList<User>) oin.readObject();
									
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
			
		return users;
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
}
