package domain;

import interfaces.ServerThreadInterface;
import network.ClientMessage;
import network.ServerMessage;
import network.ServerSocketNetwork;
import parsers.ClientMessageParser;

public class ServerThread extends Thread implements ServerThreadInterface {
	
	private ServerThreadContext serverThreadContext;
	
	
	public ServerThread(ServerThreadContext serverThreadContext) {
		this.serverThreadContext = serverThreadContext;
	}

	
	public void run() {
		ServerSocketNetwork serverSocketNetwork = serverThreadContext.getServerSocketNetwork();
		ClientMessage clientMessage = serverSocketNetwork.getClientMessage();
		ClientMessageParser clientMessageParser = new ClientMessageParser(clientMessage, serverSocketNetwork);
		
		ServerMessage serverMessage = clientMessageParser.processRequest();
		System.out.println(serverMessage);
		serverSocketNetwork.sendMessage(serverMessage);
	
		/*
		if(clientMessage.getMessageType().equals(MessageType.FILE))
			serverSocketNetwork.sendMessage(new ServerMessage(MessageType.OK));
		File b = serverSocketNetwork.receiveFile(clientMessage.getFileSize(), "teste1.jpeg");

		System.out.println("Mensagem: " + clientMessage);
		System.out.println("Thread terminada.");
		 */
	}

	
}
