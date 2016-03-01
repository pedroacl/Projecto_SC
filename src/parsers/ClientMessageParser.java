package parsers;


import java.io.File;

import dao.ConversationDAO;
import domain.Authentication;
import entities.ChatMessage;
import entities.Conversation;
import network.ClientMessage;
import network.MessageType;
import network.ServerMessage;
import network.ServerSocketNetwork;

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
		
		switch (clientMessage.getMessageType()) {
		case MESSAGE:
			isAuthenticated = authentication.authenticateUser(clientMessage.getUsername(), 
					clientMessage.getPassword());

			if(isAuthenticated && authentication.exists(clientMessage.getDestination())) {
				ChatMessage chatMessage = new ChatMessage(clientMessage.getUsername(),
					clientMessage.getDestination(), 
					clientMessage.getMessage(), 
					MessageType.MESSAGE);

				System.out.println("[ClientMessageParser.java] Adicionar chat message");
				System.out.println(chatMessage.getFromUser());
			
				System.out.println("[ClientMessageParser.java] " + (conversationDAO == null));
				
				conversationDAO.addChatMessage(chatMessage);
				
				Conversation conversation = conversationDAO.getConversationById(1L);
				System.out.println("Conversa: " + conversation);
				
				serverMessage = new ServerMessage(MessageType.OK);
			}
			else {
				serverMessage = new ServerMessage(MessageType.NOK);
				if(!isAuthenticated) {
					serverMessage.setContent("Password errada");
				}else
					serverMessage.setContent("Não existe esse contact");
				
			}
				
			break;
			
		case FILE:
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
				
				if(!isAuthenticated) {
					serverMessage.setContent("Password errada");
				}else
					serverMessage.setContent("Não existe esse contact");
			}
			
			break;
		case RECEIVER:
			
			switch(clientMessage.getMessage()) {
			
			case "recent":
				 if(authentication.authenticateUser(clientMessage.getUsername(), 
						clientMessage.getPassword())){
				//obtem a mensagem mais recente de todas as suas conversas
					//ir a sua pasta /users/getFrom()
					//Ir ao ficheiro conversations e retirar os ids de todas as conversas
					//ir a pasta das conversas /consersation e por cada id retirar a mensagem mais recente
				//colocar essa lista no serverMessagem serverNetwork.setMessages(List)
					 serverMessage = new ServerMessage(MessageType.OK);
				 }
				 else {
					 serverMessage = new ServerMessage(MessageType.NOK);
					 serverMessage.setContent("Password errada");
				 }
				 break;
			case "all" :
				if(!authentication.authenticateUser(clientMessage.getUsername(), 
						clientMessage.getPassword())) {
					
					serverMessage = new ServerMessage(MessageType.NOK);
					serverMessage.setContent("Password errada");
				}
				else {
					if(authentication.exists(clientMessage.getDestination())) {
						//ir buscar todas os ids de conversas deste utilizador
						// sacar aquele que tem com getDestination()
						//ir buscar toda a conversação
						serverMessage = new ServerMessage(MessageType.OK);
						//serverMessage.setMessages(list);
					}
					else {
						serverMessage = new ServerMessage(MessageType.NOK);
						serverMessage.setContent("Não existe esse contact");
					}			
				}
				break;
				default:
					if(!authentication.authenticateUser(clientMessage.getUsername(), 
							clientMessage.getPassword())) {
						
						serverMessage = new ServerMessage(MessageType.NOK);
						serverMessage.setContent("Password errada");
					}
					else {
						if(authentication.exists(clientMessage.getDestination())
								/*&& conversationDAO.existFile(clientMessage.getMessage())*/) {
							/*
							File file = new File (conversationDAO.getPath())
							serverMessage = new ServerMessage(MessageType.File);
							serverMessage.setFileSize(file.length())
							serverMessage.setMessage(conversationDAO.getPath())
							ssn.sendFile(ServerMessage);
							
							
							*/
						}
						else {
							serverMessage = new ServerMessage(MessageType.NOK);
							serverMessage.setContent("Não existe esse contact");
						}
					}
					
					
			}
			break;
		case ADDUSER:
			break;
		case REMOVEUSER:
			break;
		
		default:
			serverMessage = new ServerMessage(MessageType.NOK);
			serverMessage.setContent("erro");
			break;
		}
		
		return serverMessage;
	}
}
