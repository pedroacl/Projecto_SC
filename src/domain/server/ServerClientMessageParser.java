package domain.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import exceptions.InvalidMacException;
import exceptions.InvalidPasswordException;
import network.managers.ServerNetworkManager;
import network.messages.ChatMessage;
import network.messages.ClientNetworkMessage;
import network.messages.MessageType;
import network.messages.NetworkMessage;
import network.messages.ServerMessage;
import network.messages.ServerNetworkContactTypeMessage;
import security.SecurityUtils;
import service.ConversationService;
import service.GroupService;

/**
 * Classe que analisa a mensagem de rede com o pedido do cliente
 * 
 * @author Pedro, José e António
 *
 */
public class ServerClientMessageParser {

	private ClientNetworkMessage	clientMessage;

	private Authentication			authentication;

	private GroupService			groupService;

	private ConversationService		conversationService;

	private ServerNetworkManager	serverNetworkManager;

	private final int				MAX_FILE_SIZE	= Integer.MAX_VALUE;

	private final String			USERS_MAC_FILE	= "users.mac.txt";

	public ServerClientMessageParser(ClientNetworkMessage clientMessage, ServerNetworkManager serverNetworkManager,
			Authentication authentication) {

		this.clientMessage = clientMessage;
		this.authentication = authentication;
		this.serverNetworkManager = serverNetworkManager;

		conversationService = new ConversationService();
		groupService = GroupService.getInstance();

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

		// preenche sermessage com indicaçao do erro
		try {
			authentication.authenticateUser(clientMessage.getUsername(), clientMessage.getPassword());

		} catch (InvalidMacException e) {
			System.out.println("Mac inválido");
			// preenche sermessage com indicaçao do erro
			serverMessage = new ServerNetworkContactTypeMessage(MessageType.NOK);
			serverMessage.setContent("MAC inválido");

			e.printStackTrace();

			return serverMessage;

		} catch (InvalidPasswordException e) {
			serverMessage = new ServerNetworkContactTypeMessage(MessageType.NOK);
			System.out.println("[ServerClientMessageParser] - Password errada!");
			serverMessage.setContent("Password errada");

			e.printStackTrace();

			return serverMessage;
		}

		System.out.println("Server - " + clientMessage);

		switch (clientMessage.getMessageType()) {
		// mensagem de texto
		case MESSAGE:
			
			System.out.println("[ServerClientMessageParser] MESSAGE");

			// destinatario eh um utilizador ou grupo
			ServerNetworkContactTypeMessage auxServerNetworkContactTypeMessage = (ServerNetworkContactTypeMessage) verifyContactType();
			serverMessage = auxServerNetworkContactTypeMessage;

			if (!serverMessage.getMessageType().equals(MessageType.NOK))
				// envia mensagem com indicaçao grupo ou utilizador
				serverNetworkManager.sendMessage(serverMessage);

			// espera nova mensagem com AD, Ks(M), e Map<user,Kpub(Ks)>
			ChatMessage clientPGPMessage = (ChatMessage) serverNetworkManager.receiveMessage();
			clientPGPMessage.setCreatedAt(new Date());

			// guarda a mensagem
			conversationService.addChatMessage(clientPGPMessage);

			// cria message de resposta ok-> tudo correu bem
			serverMessage = new ServerNetworkContactTypeMessage(MessageType.OK);
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
					recent.add(conversationService.getLastChatMessage(id));// TODO
																			// tb
																			// preencher
																			// AD
																			// e
																			// Ks
				}
				ServerMessage serverMessageaux = new ServerMessage(MessageType.LAST_MESSAGES);
				serverMessageaux.setMessages(recent);

				serverMessage = serverMessageaux;

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
								.getAllMessagesFromConversation(conversationId); // TODO
																					// prencher
																					// AD
																					// e
																					// KS

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
						// get assinatura digital
						// get SecretKey cifrada com publicKey

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

	private NetworkMessage verifyContactType() {

		NetworkMessage serverMessage;

		// destinatario eh um utilizador
		if (authentication.existsUser(clientMessage.getDestination())) {
			ServerNetworkContactTypeMessage serverContactTypeMessage = new ServerNetworkContactTypeMessage(
					MessageType.CONTACT);

			serverContactTypeMessage.addGroupMember(clientMessage.getDestination());

			serverMessage = serverContactTypeMessage;

			// distinataio é um gropo
		} else if (groupService.existsGroup(clientMessage.getDestination())) {

			ServerNetworkContactTypeMessage serverContactTypeMessage = new ServerNetworkContactTypeMessage(
					MessageType.CONTACT);

			// obter membros do grupo
			List<String> members = groupService.getGroupMembers(clientMessage.getDestination());

			// iterar sobre a lista de membros
			// colocando-os com o respectivo Certificados na hashMap da Message
			// TODO

			/*
			 * serverContactTypeMessage.addGroupMember("jose", null);
			 * serverContactTypeMessage.addGroupMember("pedro", null);
			 * serverContactTypeMessage.addGroupMember("antonio", null);
			 */
			serverMessage = serverContactTypeMessage;
			// serverMessage.setGroupMembers(groupMembers);

			// destinatario nao existe
		} else {
			serverMessage = new ServerNetworkContactTypeMessage(MessageType.NOK);
			serverMessage.setContent("Não existe esse contact");
		}

		return serverMessage;
	}

	/**
	 * Função que recebe e guarda um ficheiro vindo do utilizador Servidor
	 * Cliente <---------AUTH------------- --------CONTACT/NOK-------> <--FILE:
	 * AD, Ks, SizeFile--- -----------OK/NOK---------->
	 * <---------Ks(FILE)---------- --------------------------->
	 * 
	 */
	private NetworkMessage receiveFile() {

		NetworkMessage serverMessage;

		// destinatario eh um utilizador ou grupo
		serverMessage = verifyContactType();

		if (!serverMessage.getMessageType().equals(MessageType.NOK)) {
			// envia mensagem com indicaçao grupo ou utilizador
			serverNetworkManager.sendMessage(serverMessage);

			// espera nova mensagem com AD,e Map<user,Kpub(Ks)>, e SizeFile
			// espera pelo ficheiro
			ChatMessage clientPGPMessage = (ChatMessage) serverNetworkManager.receiveMessage();
			clientPGPMessage.setCreatedAt(new Date());

			// obter nome do ficheiro
			String fileName = extractName(clientPGPMessage.getContent());

			// guarda a mensagem
			conversationService.addChatMessage(clientPGPMessage);

			// persiste chatMessage
			Long conversationID = conversationService.addChatMessage(clientPGPMessage);
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
				// APAGAR FICHEIRO e DADOS DESTA ULTIMA MESSAGEM?
			}
		}

		return serverMessage;
	}

}
