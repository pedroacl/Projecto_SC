package parsers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import domain.Authentication;
import entities.ChatMessage;
import network.ClientMessage;
import network.MessageType;
import network.NetworkManager;
import network.ServerMessage;
import network.ServerNetworkManager;
import service.ConversationService;
import service.GroupService;

/**
 * Classe que analisa a mensagem de rede com o pedido do cliente
 * 
 * @author Pedro, Jose, Antonio
 *
 */
public class ClientMessageParser {

	private ClientMessage clientMessage;

	private Authentication authentication;

	private GroupService groupService;
	
	private ConversationService conversationService;

	private ServerNetworkManager serverNetworkManager;

	private final int MAX_FILE_SIZE = Integer.MAX_VALUE;
	
	
	public ClientMessageParser(ClientMessage clientMessage, ServerNetworkManager serverNetworkManager) {
		this.clientMessage = clientMessage;
		authentication = Authentication.getInstance();

		conversationService = new ConversationService();
		groupService = GroupService.getInstance(); 

		this.serverNetworkManager = serverNetworkManager;
	}
	
	/**
	 * Processa a Mensagem do cliente, executa o pedido e cria mensagem de resposta
	 * @return ServerMessage com a resposta do servidor ao cliente
	 */
	public ServerMessage processRequest() {
		ServerMessage serverMessage = null;

		if (!authentication.authenticateUser(clientMessage.getUsername(), clientMessage.getPassword())) {

			serverMessage = new ServerMessage(MessageType.NOK);
			serverMessage.setContent("password Errada");

			return serverMessage;
		}

		switch (clientMessage.getMessageType()) {
		case MESSAGE:
			serverMessage = saveMessage();
			break;

		case FILE:
			serverMessage = receiveFile();
			break;
			
		case RECEIVER:

			switch (clientMessage.getContent()) {

			case "recent":

				ArrayList<Long> ids = conversationService.getAllConversationsFrom(clientMessage.getUsername());
				ArrayList<ChatMessage> recent = new ArrayList<ChatMessage>();
				for (long id : ids) {
					recent.add(conversationService.getLastChatMessage(id));
				}
				serverMessage = new ServerMessage(MessageType.LAST_MESSAGES);
				serverMessage.setMessages(recent);

				break;

			case "all":
				if (authentication.existsUser(clientMessage.getDestination()) || 
						groupService.existsGroup(clientMessage.getDestination())) {
					
					Long conversationId = conversationService.getConversationInCommom(clientMessage.getUsername(),
							clientMessage.getDestination());
					
					// se existir conversa em comum
					if (conversationId != -1) {
						ArrayList<ChatMessage> messages = (ArrayList<ChatMessage>) conversationService
								.getAllMessagesFromConversation(conversationId);

						serverMessage = new ServerMessage(MessageType.CONVERSATION);
						serverMessage.setMessages(messages);
					} else {
						serverMessage = new ServerMessage(MessageType.NOK);
						serverMessage.setContent("Não há registos desta conversa");
					}

				} else {
					serverMessage = new ServerMessage(MessageType.NOK);
					serverMessage.setContent("Não existe esse contact");
				}
				break;
			default:
				
				if (authentication.existsUser(clientMessage.getDestination()) || 
						groupService.existsGroup(clientMessage.getDestination())) {
					//verifica se exite o path
					String path = conversationService.existsFile(clientMessage.getUsername(), clientMessage.getDestination(),
							clientMessage.getContent());
					
					System.out.println("[CASE RECEIVER file]: " + path);

					// se exitir o path
					if (path != null) {

						File file = new File(path);
						serverMessage = new ServerMessage(MessageType.FILE);
						serverMessage.setFileSize((int) file.length());
						System.out.print("[ProcessRequest] -r ");
						System.out.println("file: " + path + " size = " + file.length());
						serverMessage.setContent(path);	
							
					} 
					else {
						serverMessage = new ServerMessage(MessageType.NOK);
						serverMessage.setContent("Não há registos desta conversa");
					}
					
				} 
				else {
					serverMessage = new ServerMessage(MessageType.NOK);
					serverMessage.setContent("Não existe esse contact");
				}
			}

			break;
		case ADDUSER:
			serverMessage = addUserToGroup();
			break;

		case REMOVEUSER:
			serverMessage = removeUserFromGroup();
			break;

		default:
			serverMessage = new ServerMessage(MessageType.NOK);
			serverMessage.setContent("erro");
			break;
		}

		return serverMessage;
	}

	/**
	 * Função que remove um utilizador de um grupo
	 * 
	 */
	private ServerMessage removeUserFromGroup() {
		ServerMessage serverMessage = new ServerMessage(MessageType.OK);

		// utilizador a ser removido existe
		if (authentication.existsUser(clientMessage.getDestination())) {

			groupService.removeUserFromGroup(clientMessage.getUsername(), clientMessage.getDestination(),
					clientMessage.getContent());

		} else {
			serverMessage = new ServerMessage(MessageType.NOK);
			serverMessage.setContent("Não existe esse utilizador");
		}

		return serverMessage;

	}
	
	// Devolve só o nome do ficheiro, não o path completo
	private String extractName(String absolutePath) {
		String[] splitName = absolutePath.split("/");
		return splitName[splitName.length - 1];
	}

	/**
	 * Função que permite adicionar um utilizador a um determinado grupo
	 */
	private ServerMessage addUserToGroup() {
		ServerMessage serverMessage = new ServerMessage(MessageType.OK);

		if (authentication.existsUser(clientMessage.getDestination()) && groupService.addUserToGroup(
				clientMessage.getUsername(), clientMessage.getDestination(), clientMessage.getContent())) {

		} else {
			serverMessage = new ServerMessage(MessageType.NOK);
			serverMessage.setContent("Não foi possivel adicionar utilizador ao grupo");
		}

		return serverMessage;
	}
	
	/**
	 * Função que guarda um ficheiro vindo do utilizador
	 */
	private ServerMessage receiveFile() {
		
		ServerMessage serverMessage;
		
		//verifica se o user de destino existe e que o ficheiro tem tamnho válido
		if ( (authentication.existsUser(clientMessage.getDestination()) || 
				groupService.existsGroup(clientMessage.getDestination())) 
				&& clientMessage.getFileSize() < MAX_FILE_SIZE) {
			
			//cria chatMessage para persistir 
			ChatMessage chatMessage = new ChatMessage(clientMessage.getUsername(), clientMessage.getDestination(),
					clientMessage.getContent(), MessageType.FILE);
			
			//persiste chatMessage
			Long conversationID = conversationService.addChatMessage(chatMessage);

			String fileName = extractName(clientMessage.getContent());
			System.out.println("[ProcessRequest]: extractName = " + fileName);
			String path = conversationService.getFilePath(fileName, conversationID);
			System.out.println("[ProcessRequest]: pathParaFile = " + path);
			
			//confirma ao cliente que é possivel receber o ficheiro
			serverMessage = new ServerMessage(MessageType.OK);
			serverNetworkManager.sendMessage(serverMessage);

			//recebe ficheiro
			File file = null;

			try {
				file = serverNetworkManager.receiveFile(clientMessage.getFileSize(), path);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			//verifica se o ficheiro foi bem recebido
			if (file.length() >= clientMessage.getFileSize())
				serverMessage = new ServerMessage(MessageType.OK);
			else {
				serverMessage = new ServerMessage(MessageType.NOK);
				serverMessage.setContent("Ficheiro com erro");

				// Apagar ficheiro corrompido???
				Path pathFile = file.toPath();
				try {
					Files.deleteIfExists(pathFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			serverMessage = new ServerMessage(MessageType.NOK);
			serverMessage.setContent("Não existe esse contact");
		}
		
		return serverMessage;
	}
	
	/**
	 * Função que processa e guarda um mensagem do utilizador
	 */
	private ServerMessage saveMessage() {
		
		ServerMessage serverMessage;
		
		//verifica se o user de destino existe
		if (authentication.existsUser(clientMessage.getDestination()) || 
				groupService.existsGroup(clientMessage.getDestination())) {
			
			System.out.println("[ProcessRequest-CMParser]: " + clientMessage.getContent());
			
			//cria chatMessage a guardar
			ChatMessage chatMessage = new ChatMessage(clientMessage.getUsername(), clientMessage.getDestination(),
					clientMessage.getContent(), MessageType.MESSAGE);

			System.out.println("[ClientMessageParser.java] Adicionar chat message");
			System.out.println(chatMessage.getFromUser());
			
			//persiste chatMessage
			conversationService.addChatMessage(chatMessage);
			serverMessage = new ServerMessage(MessageType.OK);
			
		} else {
			serverMessage = new ServerMessage(MessageType.NOK);
			serverMessage.setContent("Não existe esse contact");
		}
		
		return serverMessage;
		
	}
}
