package domain.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.ArrayList;

import network.managers.ServerNetworkManager;
import network.messages.ChatMessage;
import network.messages.ClientNetworkMessage;
import network.messages.MessageType;
import network.messages.NetworkMessage;
import network.messages.ServerMessage;
import network.messages.ServerNetworkContactTypeMessage;
import security.Security;
import service.ConversationService;
import service.GroupService;

/**
 * Classe que analisa a mensagem de rede com o pedido do cliente
 * 
 * @author Pedro, José e António
 *
 */
public class ServerClientMessageParser {

	private ClientNetworkMessage clientMessage;

	private Authentication authentication;

	private GroupService groupService;

	private ConversationService conversationService;

	private ServerNetworkManager serverNetworkManager;

	private final int MAX_FILE_SIZE = Integer.MAX_VALUE;

	public ServerClientMessageParser(ClientNetworkMessage clientMessage, ServerNetworkManager serverNetworkManager) {
		this.clientMessage = clientMessage;
		authentication = Authentication.getInstance();

		conversationService = new ConversationService();
		groupService = GroupService.getInstance();

		this.serverNetworkManager = serverNetworkManager;
	}

	/**
	 * Processa a Mensagem do cliente, executa o pedido e cria mensagem de
	 * resposta
	 * 
	 * @return ServerMessage com a resposta do servidor ao cliente
	 */
	public NetworkMessage processRequest() {
		NetworkMessage serverMessage = null;

		System.out.println("Server - Recebi msg");

		// erro de autenticacao
		if (!authentication.authenticateUser(clientMessage.getUsername(), clientMessage.getPassword())) {
			ServerNetworkContactTypeMessage auxMessage = new ServerNetworkContactTypeMessage(MessageType.NOK);
			auxMessage.setContent("Password errada");
			System.out.println("Server - Password errada!");
			serverMessage = auxMessage;

			return serverMessage;
		}

		System.out.println("Server - " + clientMessage);

		// obter chave assimétrica do utilizador
		KeyPair keyPair = Security.getKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();

		switch (clientMessage.getMessageType()) {
		// mensagem de texto
		case MESSAGE:
			System.out.println("Server - Message");
			// serverMessage = saveMessage();

			// destinatario eh um utilizador ou grupo
			if (authentication.existsUser(clientMessage.getDestination())) {
				System.out.println("Server - MESSAGE - Existe utilizador");
				ServerNetworkContactTypeMessage serverContactTypeMessage = new ServerNetworkContactTypeMessage(
						MessageType.CONTACT);

				serverContactTypeMessage.addGroupMember(clientMessage.getDestination(),
						Security.getCertificate(clientMessage.getDestination()));

				serverMessage = serverContactTypeMessage;
				

				// group
			} else if (groupService.existsGroup(clientMessage.getDestination())) {
				ServerNetworkContactTypeMessage serverContactTypeMessage = new ServerNetworkContactTypeMessage(
						MessageType.CONTACT);
				// serverMessage = new
				// ServerContactTypeMessage(MessageType.CONTACT);

				serverContactTypeMessage.addGroupMember("jose", null);
				serverContactTypeMessage.addGroupMember("pedro", null);
				serverContactTypeMessage.addGroupMember("antonio", null);

				serverMessage = serverContactTypeMessage;
				// serverMessage.setGroupMembers(groupMembers);

				// contacto
			} else {
				serverMessage = new ServerNetworkContactTypeMessage(MessageType.NOK);
				serverMessage.setContent("Não existe esse contact");
			}

			serverNetworkManager.sendMessage(serverMessage);
			ChatMessage clientPGPMessage = (ChatMessage) serverNetworkManager.receiveMessage();
			
			System.out.println("Server - ClientPGPMessageType: " + clientPGPMessage.getMessageType());
			System.out.println("Server - Mensagem: " + clientPGPMessage.getMessage());

			break;
		// mensagem contendo um ficheiro
		case FILE:
			serverMessage = receiveFile();
			break;
		// mensagem contendo diversas mensagens
		case RECEIVER:
			switch (clientMessage.getContent()) {
			// mensagem mais recente com cada utilizador
			case "recent":
				ArrayList<Long> ids = conversationService.getAllConversationsFrom(clientMessage.getUsername());
				ArrayList<ChatMessage> recent = new ArrayList<ChatMessage>();
				for (long id : ids) {
					recent.add(conversationService.getLastChatMessage(id));
				}
				serverMessage = new ServerMessage(MessageType.LAST_MESSAGES);
				// serverMessage.setMessages(recent);

				break;

			// todas as mensagens trocadas com um utilizador
			case "all":
				// destinatario eh utilizador ou grupo
				if (authentication.existsUser(clientMessage.getDestination())
						|| groupService.existsGroup(clientMessage.getDestination())) {

					Long conversationId = conversationService.getConversationInCommom(clientMessage.getUsername(),
							clientMessage.getDestination());

					// se existir conversa em comum
					if (conversationId != -1) {
						ArrayList<ChatMessage> messages = (ArrayList<ChatMessage>) conversationService
								.getAllMessagesFromConversation(conversationId);

						serverMessage = new ServerMessage(MessageType.CONVERSATION);
						// serverMessage.setMessages(messages);
					} else {
						serverMessage = new ServerMessage(MessageType.NOK);
						serverMessage.setContent("Não há registos desta conversa");
					}
					// destinatario nao existe
				} else {
					serverMessage = new ServerMessage(MessageType.NOK);
					serverMessage.setContent("Não existe esse contact");
				}
				break;
			default:
				// destinatario eh utilizador ou grupo
				if (authentication.existsUser(clientMessage.getDestination())
						|| groupService.existsGroup(clientMessage.getDestination())) {
					// verifica se exite o path
					String path = conversationService.existsFile(clientMessage.getUsername(),
							clientMessage.getDestination(), clientMessage.getContent());

					// se exitir o path
					if (path != null) {

						File file = new File(path);
						serverMessage = new ServerMessage(MessageType.FILE);
						serverMessage.setFileSize((int) file.length());
						serverMessage.setContent(path);

					} else {
						serverMessage = new ServerMessage(MessageType.NOK);
						serverMessage.setContent("Não há registos desta conversa");
					}
					// destinatario nao existe
				} else {
					serverMessage = new ServerMessage(MessageType.NOK);
					serverMessage.setContent("Não existe esse contacto");
				}
			}

			break;
		// adicionar utilizador
		case ADDUSER:
			serverMessage = addUserToGroup();
			break;

		// remover utilizador
		case REMOVEUSER:
			serverMessage = removeUserFromGroup();
			break;

		// mensagem mal formatada
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

			if (!groupService.removeUserFromGroup(clientMessage.getUsername(), clientMessage.getDestination(),
					clientMessage.getContent())) {

				serverMessage = new ServerMessage(MessageType.NOK);
				serverMessage.setContent("Não foi possivel remover esse utilizador");
			}

		} else {
			serverMessage = new ServerMessage(MessageType.NOK);
			serverMessage.setContent("Não existe esse utilizador");
		}

		return serverMessage;

	}

	/**
	 * Função que obtem o nome do ficheiro presente num path
	 * 
	 * @param absolutePath
	 *            Caminho absoluto
	 * @return Devolve o nome do ficheiro
	 */
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

		// verifica se o user de destino existe e que o ficheiro tem tamnho
		// válido
		if ((authentication.existsUser(clientMessage.getDestination())
				|| groupService.existsGroup(clientMessage.getDestination()))
				&& clientMessage.getFileSize() < MAX_FILE_SIZE) {

			// obter nome do ficheiro
			String fileName = extractName(clientMessage.getContent());

			// cria chatMessage para persistir
			ChatMessage chatMessage = new ChatMessage(MessageType.FILE);
			chatMessage.setFromUser(clientMessage.getUsername());
			chatMessage.setDestination(clientMessage.getDestination());
			chatMessage.setContent(clientMessage.getContent());

			// persiste chatMessage
			Long conversationID = conversationService.addChatMessage(chatMessage);
			String path = conversationService.getFilePath(fileName, conversationID);

			// confirma ao cliente que é possivel receber o ficheiro
			serverMessage = new ServerMessage(MessageType.OK);
			serverNetworkManager.sendMessage(serverMessage);

			// recebe ficheiro
			File file = null;

			try {
				file = serverNetworkManager.receiveFile(clientMessage.getFileSize(), path);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// verifica se o ficheiro foi bem recebido
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

		// destinatario eh um utilizador ou grupo
		if (authentication.existsUser(clientMessage.getDestination())
				|| groupService.existsGroup(clientMessage.getDestination())) {

			// cria chatMessage a guardar
			ChatMessage chatMessage = new ChatMessage(MessageType.MESSAGE);
			chatMessage.setFromUser(clientMessage.getUsername());
			chatMessage.setDestination(clientMessage.getDestination());
			chatMessage.setContent(clientMessage.getContent());

			// persiste chatMessage
			conversationService.addChatMessage(chatMessage);
			serverMessage = new ServerMessage(MessageType.OK);

			// destinatario nao existe
		} else {
			serverMessage = new ServerMessage(MessageType.NOK);
			serverMessage.setContent("Não existe esse contact");
		}

		return serverMessage;
	}
}
