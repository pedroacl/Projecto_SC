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
import service.ConversationService;
import service.GroupService;
import util.MiscUtil;
import util.SecurityUtils;

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

	private final String USERS_FILE = "users.txt";

	private final String GROUPS_FILE = "groups.txt";

	public ServerClientMessageParser(ClientNetworkMessage clientMessage, ServerNetworkManager serverNetworkManager,
			Authentication authentication) throws InvalidMacException {

		this.clientMessage = clientMessage;
		this.authentication = authentication;
		this.serverNetworkManager = serverNetworkManager;

		conversationService = new ConversationService();
		groupService = new GroupService(authentication.getServerPassword());

	}

	/**
	 * Processa a Mensagem do cliente, executa o pedido e cria mensagem de
	 * resposta
	 * 
	 * @return ServerMessage com a resposta do servidor ao cliente
	 */
	public NetworkMessage processRequest() {
		NetworkMessage serverMessage = null;

		// preenche sermessage com indicaçao do erro
		try {
			authentication.authenticateUser(clientMessage.getUsername(), clientMessage.getPassword());

		} catch (InvalidMacException e) {
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

		// avaliar tipo de pedido do cliente
		switch (clientMessage.getMessageType()) {

		/**
		 * Mensagem de texto
		 */
		case MESSAGE:

			System.out.println("[ServerClientMessageParser] MESSAGE");

			// destinatario eh um utilizador ou grupo
			ServerNetworkContactTypeMessage auxServerNetworkContactTypeMessage = (ServerNetworkContactTypeMessage) verifyContactType();
			serverMessage = auxServerNetworkContactTypeMessage;

			if (!serverMessage.getMessageType().equals(MessageType.NOK)) {
				// envia mensagem com indicaçao grupo ou utilizador
				serverNetworkManager.sendMessage(serverMessage);

				System.out.println(serverMessage.getMessageType());

				// espera nova mensagem com AD, Ks(M), e Map<user,Kpub(Ks)>
				ChatMessage clientPGPMessage = (ChatMessage) serverNetworkManager.receiveMessage();
				System.out.println("[ServerClientMessageParser] message: " + clientPGPMessage.getCypheredMessage());

				clientPGPMessage.setCreatedAt(new Date());

				// guarda a mensagem
				conversationService.addChatMessage(clientPGPMessage);

				// cria message de resposta ok-> tudo correu bem
				serverMessage = new ServerNetworkContactTypeMessage(MessageType.OK);
			}

			break;

		/**
		 * Mensagem contendo um ficheiro
		 */
		case FILE:
			serverMessage = receiveFile();
			break;
			
		/**
		 * Mensagem contendo diversas mensagens
		 */
		case RECEIVER:

			switch (clientMessage.getContent()) {
			// mensagem mais recente com cada utilizador
			case "recent":
				System.out.println("[ServerClientMessageParser] pedido do tipo -r");
				
				System.out.println("[ServerClientMessageParser]: "+ clientMessage.getUsername());
				
				ArrayList<Long> conversationsIds = conversationService.getAllConversationsFrom(clientMessage.getUsername());
				ArrayList<ChatMessage> recent = new ArrayList<ChatMessage>();

				for (long conversationId : conversationsIds) {
					ChatMessage lastChatMessage = conversationService.getLastChatMessage(clientMessage.getUsername(),
							conversationId);

					if (lastChatMessage != null)
						recent.add(lastChatMessage);
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
								.getAllMessagesFromConversation(clientMessage.getUsername(),conversationId);														

						ServerMessage serverMessageaux2 = new ServerMessage(MessageType.CONVERSATION);
						serverMessageaux2.setMessages(messages);
						
						serverMessage = serverMessageaux2;
						
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
				
				/*
				 * S ----------RECEIVER-----------> C
				 *  --AD,Ks(F),K(ks)/NOK--FILE-->
				 *  -----------F---------------->
				 * <----------OK/NOK-------------
				 * -----------OK/NOK-------------> 
				 * */
				
				
				// destinatario eh utilizador ou grupo
				if (authentication.existsUser(clientMessage.getDestination())
						|| groupService.existsGroup(clientMessage.getDestination())) {
					// verifica se exite o path
					String path = conversationService.existsFile(clientMessage.getUsername(),
							clientMessage.getDestination(), clientMessage.getContent());

					// se exitir o path
					if (path != null) {
						ChatMessage messageTosend = new ChatMessage(MessageType.FILE);
						
						File file = new File(path);
						int fileSize = (int) file.length();
						messageTosend.setFileSize(fileSize);
						String fileName = MiscUtil.extractName(path);
						messageTosend.setContent(fileName);
						
						// get assinatura digital
						Long id = Long.parseLong(path.split("/")[1]) ;
						byte [] sign = conversationService.getChatMessageSignature
								(id,fileName);
						messageTosend.setSignature(sign);
						
						//obtem criador da assinatura digital;
						String owner = ConversationService.getSignatureProducer(id,fileName);
						messageTosend.setFromUser(owner);
						
						// get SecretKey cifrada com publicKey
						byte[] key = conversationService.getUserChatMessageKey
								(clientMessage.getUsername(), id,fileName);
						messageTosend.setCypheredMessageKey(key);
						
						//envia mensagem de aviso para file
						serverNetworkManager.sendMessage(messageTosend);
						
						//envia File
						serverNetworkManager.sendFile(path, fileSize);
						
						//espera resposta do cliente
						ChatMessage clientPGPMessage = (ChatMessage) serverNetworkManager.receiveMessage();
						
						//envia confimaçao ao cliente
						serverMessage = new ServerMessage(clientPGPMessage.getMessageType());
								

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

		/**
		 * Adicionar um utilizador a um grupo
		 */
		case ADDUSER:
			try {
				serverMessage = addUserToGroup();
			} catch (InvalidMacException e) {
				e.printStackTrace();
			}
			
			System.out.println("[ServerClientMesageParser] addUser: " + serverMessage.getMessageType());
			break;

		/**
		 * Remover um utilizador de um grupo
		 */
		case REMOVEUSER:
			try {
				serverMessage = removeUserFromGroup();
			} catch (InvalidMacException e) {
				e.printStackTrace();
			}

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
	 * @throws InvalidMacException
	 * 
	 */
	private ServerMessage removeUserFromGroup() throws InvalidMacException {
		ServerMessage serverMessage = new ServerMessage(MessageType.OK);

		// utilizador a ser removido existe
		if (authentication.existsUser(clientMessage.getDestination())) {
			SecurityUtils.validateFileMac(GROUPS_FILE, this.authentication.getServerPassword());

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
	 * Função que permite adicionar um utilizador a um determinado grupo
	 * 
	 * @throws InvalidMacException
	 */
	private ServerMessage addUserToGroup() throws InvalidMacException {
		ServerMessage serverMessage = new ServerMessage(MessageType.OK);

		SecurityUtils.validateFileMac(GROUPS_FILE, authentication.getServerPassword());

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

			for(String membro: members) {
				if(!membro.equals(clientMessage.getUsername()))
					serverContactTypeMessage.addGroupMember(membro);
			}
			
			serverMessage = serverContactTypeMessage;

			// destinatario nao existe
		} else {
			serverMessage = new ServerNetworkContactTypeMessage(MessageType.NOK);
			serverMessage.setContent("Não existe esse contact");
		}

		return serverMessage;
	}

	/**
	 * Função que recebe e guarda um ficheiro vindo do utilizador 
	 * Servidor	<---------FILE-------------- Cliente
	 * 			---------CONTACT/NOK-------> 
	 *         	<--FILE: AD, Ks, SizeFile---
	 *          -----------OK/NOK---------->
	 * 			<---------Ks(FILE)---------- 
	 * 			--------------------------->
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
			String fileName = clientPGPMessage.getContent();
			
			System.out.println("[ServerClientMessageParser]: fileName: " + fileName);
			System.out.println("[ServerClientMessageParser]: Destination: " + clientPGPMessage.getDestination());

			// persiste chatMessage
			Long conversationID = conversationService.addChatMessage(clientPGPMessage);
			String path = conversationService.getFilePath(fileName, conversationID);

			// confirma ao cliente que é possivel receber o ficheiro
			serverMessage = new ServerMessage(MessageType.OK);
			serverNetworkManager.sendMessage(serverMessage);

			// recebe ficheiro
			File file = null;

			try {
				file = serverNetworkManager.receiveFile(clientPGPMessage .getFileSize(), path);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			System.out.println("[ServerClientMessageParser] FILE:"+ file.length() );

			// verifica se o ficheiro foi bem recebido
			if (file.length() >= clientMessage.getFileSize()) {
				System.out.println("[ServerClientMessageParser] FILE: " + serverMessage.getContent());
				serverMessage = new ServerMessage(MessageType.OK);
			}

			else {
				serverMessage = new ServerMessage(MessageType.NOK);
				serverMessage.setContent("Ficheiro com erro");
				// APAGAR FICHEIRO e DADOS DESTA ULTIMA MESSAGEM?
			}
		}

		return serverMessage;
	}

}
