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
import network.ServerMessage;
import network.ServerSocketNetwork;
import service.ConversationService;
import service.GroupService;

public class ClientMessageParser {

	private ClientMessage clientMessage;

	private Authentication authentication;

	private GroupService groupService;
	
	private ConversationService conversationService;

	private ServerSocketNetwork ssn;

	private final int MAX_FILE_SIZE = Integer.MAX_VALUE;

	public ClientMessageParser(ClientMessage clientMessage, ServerSocketNetwork serverSocketNetwork) {
		this.clientMessage = clientMessage;
		authentication = Authentication.getInstance();

		conversationService = new ConversationService();
		groupService = new GroupService();

		this.ssn = serverSocketNetwork;
	}

	public ServerMessage processRequest() {
		ServerMessage serverMessage = null;

		if (!authentication.authenticateUser(clientMessage.getUsername(), clientMessage.getPassword())) {

			serverMessage = new ServerMessage(MessageType.NOK);
			serverMessage.setContent("password Errada");

			return serverMessage;
		}

		switch (clientMessage.getMessageType()) {
		case MESSAGE:

			if (authentication.existsUser(clientMessage.getDestination()) || 
					groupService.existsGroup(clientMessage.getDestination())) {
				System.out.println("[ProcessRequest-CMParser]: " + clientMessage.getMessage());
				ChatMessage chatMessage = new ChatMessage(clientMessage.getUsername(), clientMessage.getDestination(),
						clientMessage.getMessage(), MessageType.MESSAGE);

				System.out.println("[ClientMessageParser.java] Adicionar chat message");
				System.out.println(chatMessage.getFromUser());

				conversationService.addChatMessage(chatMessage);

				serverMessage = new ServerMessage(MessageType.OK);
			} else {
				serverMessage = new ServerMessage(MessageType.NOK);
				serverMessage.setContent("Não existe esse contact");
			}

			break;

		case FILE:
			if ( (authentication.existsUser(clientMessage.getDestination()) || 
					groupService.existsGroup(clientMessage.getDestination())) 
					&& clientMessage.getFileSize() < MAX_FILE_SIZE) {

				ChatMessage chatMessage = new ChatMessage(clientMessage.getUsername(), clientMessage.getDestination(),
						clientMessage.getMessage(), MessageType.FILE);

				Long conversationID = conversationService.addChatMessage(chatMessage);

				String fileName = extractName(clientMessage.getMessage());
				System.out.println("[ProcessRequest]: extractName = " + fileName);
				String path = conversationService.getFilePath(fileName, conversationID);
				System.out.println("[ProcessRequest]: pathParaFile = " + path);

				serverMessage = new ServerMessage(MessageType.OK);
				ssn.sendMessage(serverMessage);

				File file = ssn.receiveFile(clientMessage.getFileSize(), path);

				if (file.length() == clientMessage.getFileSize())
					serverMessage = new ServerMessage(MessageType.OK);
				else {
					serverMessage = new ServerMessage(MessageType.NOK);

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

			break;
		case RECEIVER:

			switch (clientMessage.getMessage()) {

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
				System.out.println("Print toUser:" + clientMessage.getDestination());
				if (authentication.existsUser(clientMessage.getDestination()) || 
						groupService.existsGroup(clientMessage.getDestination())) {
					//verifica se exite o path
					String path = conversationService.existsFile(clientMessage.getUsername(), clientMessage.getDestination(),
							clientMessage.getMessage());
					
					System.out.println("[CASE RECEIVER file]: " + path);

					// se exitir o path
					if (path != null) {

						File file = new File(path);
						serverMessage = new ServerMessage(MessageType.FILE);
						serverMessage.setSizeFile((int) file.length());
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
	 * @return
	 */
	private ServerMessage removeUserFromGroup() {
		ServerMessage serverMessage = new ServerMessage(MessageType.OK);

		// utilizador a ser removido existe
		if (authentication.existsUser(clientMessage.getDestination())) {

			groupService.removeUserFromGroup(clientMessage.getUsername(), clientMessage.getDestination(),
					clientMessage.getMessage());

		} else {
			serverMessage = new ServerMessage(MessageType.NOK);
			serverMessage.setContent("Não existe esse utilizador");
		}

		return serverMessage;

	}

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
				clientMessage.getUsername(), clientMessage.getDestination(), clientMessage.getMessage())) {

		} else {
			serverMessage = new ServerMessage(MessageType.NOK);
			serverMessage.setContent("Não foi possivel adicionar utilizador ao grupo");
		}

		return serverMessage;
	}
}
