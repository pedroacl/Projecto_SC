package domain;

import java.io.File;
import java.io.IOException;

import interfaces.ServerThreadInterface;
import network.ClientMessage;
import network.MessageType;
import network.ServerMessage;
import network.ServerSocketNetwork;

public class ServerThread extends Thread implements ServerThreadInterface {
	
	private ServerThreadContext serverThreadContext;
	
	
	public ServerThread(ServerThreadContext serverThreadContext) {
		this.serverThreadContext = serverThreadContext;
	}

	
	public void run() {
		ServerSocketNetwork serverSocketNetwork = serverThreadContext.getServerSocketNetwork();
		ClientMessage clientMessage = serverSocketNetwork.getClientMessage();
		
		if(clientMessage.getMessageType().equals(MessageType.FILE))
			serverSocketNetwork.sendMessage(new ServerMessage(MessageType.OK));
			try {
				File b =serverSocketNetwork.receiveFile(clientMessage.getFileSize(), "teste1.mp3");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		System.out.println("Mensagem: " + clientMessage);
		System.out.println("Thread terminada.");
	}

	
}
