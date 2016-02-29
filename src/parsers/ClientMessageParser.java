package parsers;


import java.io.File;

import domain.Authentication;
import entities.ChatMessage;
import entities.Conversation;
import network.ClientMessage;
import network.MessageType;
import network.ServerMessage;
import network.ServerSocketNetwork;
import persistence.ConversationDAO;

public class ClientMessageParser {
	
	private ClientMessage clientMessage;

	private Authentication authentication;
	
	private ConversationDAO conversationDAO;
	
	private ServerSocketNetwork ssn;
	
	private final int MAX_FILE_SIZE = Integer.MAX_VALUE;
	

	public ClientMessageParser(ClientMessage clientMessage, ServerSocketNetwork serverSocketNetwork) {
		this.clientMessage = clientMessage;
		authentication = Authentication.getInstance();
		conversationDAO = ConversationDAO.getInstance();
		this.ssn = serverSocketNetwork;
	}
	
	public ServerMessage processRequest() {
		ServerMessage serverMessage = null;
		
		boolean isAuthenticated = false;
		
		switch (clientMessage.getMessageType().toString()) {
		case "MESSAGE":
			isAuthenticated = authentication.authenticateUser(clientMessage.getUsername(), 
					clientMessage.getPassword());

			if(isAuthenticated && authentication.exists(clientMessage.getDestination())) {
				ChatMessage chatMessage = new ChatMessage(clientMessage.getUsername(),
					clientMessage.getDestination(), 
					clientMessage.getMessage(), 
					MessageType.MESSAGE);

				System.out.println("Adicionar chat message");
				System.out.println(chatMessage.getFromUser());
				
				conversationDAO.addChatMessage(chatMessage);
				
				Conversation conversation = conversationDAO.getConversationById(1L);
				System.out.println("Conversa: " + conversation);
				
				serverMessage = new ServerMessage(MessageType.OK);
			}
			else {
				serverMessage = new ServerMessage(MessageType.NOK);
				serverMessage.setContent("Não existe esse contact");
			}
				
			break;
			
		case "FILE":
			isAuthenticated = authentication.authenticateUser(clientMessage.getUsername(), 
					clientMessage.getPassword());

			if (isAuthenticated && authentication.exists(clientMessage.getDestination()) && 
					clientMessage.getFileSize() < MAX_FILE_SIZE) {

				String path = conversationDAO.getFilePath(clientMessage.getUsername(),
						clientMessage.getDestination(), clientMessage.getMessage());
				
				File file = ssn.receiveFile(clientMessage.getFileSize(), path);
				
				ChatMessage chatMessage = new ChatMessage(
						clientMessage.getUsername(),
						clientMessage.getDestination(), 
						clientMessage.getMessage(), 
						MessageType.FILE);

				conversationDAO.addChatMessage(chatMessage);
				serverMessage = new ServerMessage(MessageType.OK);
			}
			else {
				serverMessage = new ServerMessage(MessageType.NOK);
				serverMessage.setContent("Não existe esse contact");
			}
			
			break;

		default:
			break;
		}
		
		return serverMessage;
	}
}
