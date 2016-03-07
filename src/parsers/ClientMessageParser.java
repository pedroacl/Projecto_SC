package parsers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import dao.ConversationDAO;
import dao.GroupDAO;
import domain.Authentication;
import entities.ChatMessage;
import entities.Group;
import network.ClientMessage;
import network.MessageType;
import network.ServerMessage;
import network.ServerSocketNetwork;
import util.MiscUtil;

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
			isAuthenticated = authentication.authenticateUser(clientMessage.getUsername(), clientMessage.getPassword());

			if (isAuthenticated && authentication.exists(clientMessage.getDestination())) {
				System.out.println("[ProcessRequest-CMParser]: " + clientMessage.getMessage());
				ChatMessage chatMessage = new ChatMessage(clientMessage.getUsername(), clientMessage.getDestination(),
						clientMessage.getMessage(), MessageType.MESSAGE);

				System.out.println("[ClientMessageParser.java] Adicionar chat message");
				System.out.println(chatMessage.getFromUser());

				System.out.println("[ClientMessageParser.java] " + (conversationDAO == null));

				conversationDAO.addChatMessage(chatMessage);

				serverMessage = new ServerMessage(MessageType.OK);
			} else {
				serverMessage = new ServerMessage(MessageType.NOK);
				if (!isAuthenticated) {
					serverMessage.setContent("Password errada");
				} else
					serverMessage.setContent("Não existe esse contact");

			}

			break;

		case FILE:
			isAuthenticated = authentication.authenticateUser(clientMessage.getUsername(), clientMessage.getPassword());

			if (isAuthenticated && authentication.exists(clientMessage.getDestination())
					&& clientMessage.getFileSize() < MAX_FILE_SIZE) {

				ChatMessage chatMessage = new ChatMessage(clientMessage.getUsername(), clientMessage.getDestination(),
						clientMessage.getMessage(), MessageType.FILE);

				Long conversationID = conversationDAO.addChatMessage(chatMessage);

				String fileName = extractName(clientMessage.getMessage());
				System.out.println("[ProcessRequest]: extractName = " + fileName);
				String path = conversationDAO.getFilePath(fileName, conversationID);
				System.out.println("[ProcessRequest]: pathParaFile = " + path);

				serverMessage = new ServerMessage(MessageType.OK);
				ssn.sendMessage(serverMessage);

				File file = ssn.receiveFile(clientMessage.getFileSize(), path);

				if (file.length() == clientMessage.getFileSize())
					serverMessage = new ServerMessage(MessageType.OK);
				else {
					serverMessage = new ServerMessage(MessageType.NOK);

					// Apagar ficheiro corrumpido???
					Path pathFile = file.toPath();
					try {
						Files.deleteIfExists(pathFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				serverMessage = new ServerMessage(MessageType.NOK);

				if (!isAuthenticated) {
					serverMessage.setContent("Password errada");
				} else
					serverMessage.setContent("Não existe esse contact");
			}

			break;
		case RECEIVER:

			switch (clientMessage.getMessage()) {

			case "recent":
				if (authentication.authenticateUser(clientMessage.getUsername(), clientMessage.getPassword())) {

					ArrayList<Long> ids = conversationDAO.getAllConversationsFrom(clientMessage.getUsername());
					ArrayList<ChatMessage> recent = new ArrayList<ChatMessage>();
					for (long id : ids) {
						recent.add(conversationDAO.getLastChatMessage(id));
					}
					serverMessage = new ServerMessage(MessageType.CONVERSATION);
					serverMessage.setMessages(recent);
				} else {
					serverMessage = new ServerMessage(MessageType.NOK);
					serverMessage.setContent("Password errada");
				}
				break;
			case "all":
				if (!authentication.authenticateUser(clientMessage.getUsername(), clientMessage.getPassword())) {

					serverMessage = new ServerMessage(MessageType.NOK);
					serverMessage.setContent("Password errada");
				} else {
					if (authentication.exists(clientMessage.getDestination())) {
						Long conversationId = conversationDAO.getConversationInCommom(clientMessage.getUsername(),
								clientMessage.getDestination());
						// se existir conversa em comum
						if (conversationId != -1) {
							ArrayList<ChatMessage> messages = (ArrayList<ChatMessage>) conversationDAO
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
				}
				break;
			default:
				if (!authentication.authenticateUser(clientMessage.getUsername(), clientMessage.getPassword())) {

					serverMessage = new ServerMessage(MessageType.NOK);
					serverMessage.setContent("Password errada");
				} else {
					if (authentication.exists(clientMessage.getDestination())) {
						String path = conversationDAO.existFile(clientMessage.getUsername(),
								clientMessage.getDestination(), clientMessage.getMessage());

						// se exitir o path
						if (path != null) {

							File file = new File(path);
							serverMessage = new ServerMessage(MessageType.FILE);
							serverMessage.setSizeFile((int) file.length());
							System.out.print("[ProcessRequest] -r file:");
							System.out.println("file: " + path + "size = " + file.length());
							serverMessage.setContent(path);
							boolean sended = ssn.sendFile(serverMessage);
						} else {
							serverMessage = new ServerMessage(MessageType.NOK);
							serverMessage.setContent("Não há registos desta conversa");
						}
					} else {
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

	private String extractName(String absolutePath) {
		String[] splitName = absolutePath.split("/");
		return splitName[splitName.length - 1];
	}

	/**
	 * Função que permite adicionar um utilizador a um determinado grupo
	 */
	private void addUserToGroup() {
		boolean isAuthenticated = authentication.authenticateUser(clientMessage.getUsername(),
				clientMessage.getPassword());

		// utilizador esta autenticado e o utilizador a ser adicionado existe
		if (isAuthenticated && authentication.existsUser(clientMessage.getDestination())) {
			String groupName = clientMessage.getMessage();

			// existe grupo e o utilizador eh owner
			if (authentication.existsGroup(groupName)
					&& authentication.getGroupOwner(groupName).equals(clientMessage.getUsername())) {

				// ler ficheiro
				String filePath = "groups/" + groupName + "/group";
				Group group = (Group) MiscUtil.readObject(filePath);

				// adicionou utilizador ao grupo
				if (authentication.addUserToGroup(clientMessage.getDestination(), group)) {
					conversationDAO.addConversationToUser(clientMessage.getDestination(),
							clientMessage.getMessage(), group.getConversationId());
				}
			} else {
				// TODO criar grupo
			}
		} else {

		}
	}
}
