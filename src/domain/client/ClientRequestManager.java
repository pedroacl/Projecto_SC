package domain.client;

import java.security.KeyPair;
import java.util.Set;

import javax.crypto.SecretKey;

import network.managers.ClientNetworkManager;
import network.messages.ChatMessage;
import network.messages.ClientNetworkMessage;
import network.messages.MessageType;
import network.messages.NetworkMessage;
import network.messages.ServerNetworkContactTypeMessage;
import security.SecurityUtils;

public class ClientRequestManager {

	private Parsed parsedRequest;
	private ClientNetworkManager clientNetworkManager;
	private KeyPair keyPair;

	public ClientRequestManager(Parsed parsedRequest, ClientNetworkManager clientNetworkManager) {
		this.parsedRequest = parsedRequest;
		this.clientNetworkManager = clientNetworkManager;

		// obter chave assimétrica do utilizador
		keyPair = SecurityUtils.getKeyPair();
	}

	public NetworkMessage processRequest() {

		NetworkMessage networkMessage = null;
		ServerNetworkContactTypeMessage serverNetworkContactTypeMessage = null;
		ChatMessage chatMessage = null;
		ClientNetworkMessage clientNetworkMessage;

		switch (parsedRequest.getOrder()) {
		// client quer enviar uma mensagem
		
		/* 
		 * C --------AUTH------------> S
		 *   <------Contact/NOK-------                         
		 *   ----AD,Ks(M),Kp(Ks)----->
		 *   <-------OK/NOK----------
		 */
		
		case "-m":
			// enviar mensagem a perguntar o tipo do destinatario (contacto?
			// grupo?)
			sendAuthenticationMessage();

			// obter tipo de contacto
			serverNetworkContactTypeMessage = (ServerNetworkContactTypeMessage) clientNetworkManager.receiveMessage();

			// existe contacto
			if (serverNetworkContactTypeMessage.getMessageType() == MessageType.CONTACT) {
				ChatMessage clientPGPMessage = new ChatMessage(MessageType.MESSAGE);

				System.out.println(serverNetworkContactTypeMessage.numGroupMembers());

				// contacto
				System.out.println("Client - CONTACT");

				// gerar assinatura
				byte[] clientSignature = SecurityUtils.signMessage(parsedRequest.getSpecificField(),
						keyPair.getPrivate());
				clientPGPMessage.setSignature(clientSignature);

				// obter chave secreta
				SecretKey secretKey = SecurityUtils.generateSecretKey();

				// cifrar mensagem com chave secreta
				byte[] encryptedMessage = SecurityUtils.cipherWithSecretKey(parsedRequest.getSpecificField().getBytes(),
						secretKey);

				clientPGPMessage.setMessage(encryptedMessage);
				System.out.println("Client - " + encryptedMessage);

				Set<String> groupMembers = (Set<String>) serverNetworkContactTypeMessage.getGroupMembers();

				// cifrar chave secreta, usada para cifrar mensagem anterior
				for (String username : groupMembers) {
					// wrap da chave secreta a ser enviada com a chave
					// publica do contacto de destino
					byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(secretKey,
							serverNetworkContactTypeMessage.getCertificate(username));

					clientPGPMessage.putUserKey(username, wrappedSecretKey);
				}

				// adicionar chave cifrada do proprio utilizador
				byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(secretKey,
						SecurityUtils.getCertificate(parsedRequest.getUsername()));

				clientPGPMessage.putUserKey(parsedRequest.getUsername(), wrappedSecretKey);

				// enviar mensagem
				clientNetworkManager.sendMessage(clientPGPMessage);

				// espera resposta
				networkMessage = (NetworkMessage) clientNetworkManager.receiveMessage();

			} else {
				// nao existe contact, devolve erro
				networkMessage = serverNetworkContactTypeMessage;

			}
			break;
			
			/* 
			 * C --------AUTH------------> S
			 *   <------Contact/NOK-------                         
			 *   ----AD,Ks(M),Kp(Ks)----->
			 *   -------file------------->
			 *   <--------OK/NOK----------
			 */

		case "-f":
			// enviar mensagem a perguntar o tipo do destinatario (user? grupo?)
			sendAuthenticationMessage();

			// obter tipo de contacto
			serverNetworkContactTypeMessage = (ServerNetworkContactTypeMessage) clientNetworkManager.receiveMessage();

			// existe contacto
			if (serverNetworkContactTypeMessage.getMessageType() == MessageType.CONTACT) {

				chatMessage = new ChatMessage(MessageType.FILE);

				System.out.println(serverNetworkContactTypeMessage.numGroupMembers());

				// contacto
				System.out.println("Client - CONTACT");

				// gerar assinatura
				byte[] clientSignature = SecurityUtils.signFile(parsedRequest.getSpecificField(), keyPair.getPrivate());
				chatMessage.setSignature(clientSignature);

				// obter chave secreta
				SecretKey secretKey = SecurityUtils.generateSecretKey();

				// envia tamanho do ficheiro
				chatMessage.setFileSize(parsedRequest.getFileSize());

				Set<String> groupMembers = (Set<String>) serverNetworkContactTypeMessage.getGroupMembers();

				// cifrar chave secreta, usada para cifrar ficheiro
				for (String username : groupMembers) {
					// wrap da chave secreta a ser enviada com a chave
					// publica do contacto de destino
					byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(secretKey,
							serverNetworkContactTypeMessage.getCertificate(username));
					chatMessage.putUserKey(username, wrappedSecretKey);
				}

				// adicionar chave cifrada do proprio utilizador
				byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(secretKey,
						SecurityUtils.getCertificate(parsedRequest.getUsername()));

				chatMessage.putUserKey(parsedRequest.getUsername(), wrappedSecretKey);

				// enviar mensagem
				clientNetworkManager.sendMessageAndFile(chatMessage, secretKey);

				// espera resposta
				networkMessage = (NetworkMessage) clientNetworkManager.receiveMessage();

			} else {

				// nao existe contact, devolve erro
				networkMessage = serverNetworkContactTypeMessage;
			}
			break;
			
			/* 
			 * C ----------RECEIVER-----------> S
			 *   <--AD,Ks(M),K(k), M/NOK--LAST--                       
			 */

		case "rLast":

			clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(), parsedRequest.getPassword(),
					MessageType.RECEIVER);

			clientNetworkMessage.setContent("recent");

			// enviar mensagem
			clientNetworkManager.sendMessage(clientNetworkMessage);

			// espera resposta
			ChatMessage chatmessage = (ChatMessage) clientNetworkManager.receiveMessage();

			networkMessage = chatmessage;
			break;
			
			

		case "rContact":
			
			/* 
			 * C -------------RECEIVER--------------> S
			 *   <--AD,Ks(M),K(k), M/NOK--CONVERSATION--                       
			 */

			clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(), parsedRequest.getPassword(),
					MessageType.RECEIVER);

			clientNetworkMessage.setContent("all");

			// enviar mensagem
			clientNetworkManager.sendMessage(clientNetworkMessage);

			// espera resposta
			chatmessage = (ChatMessage) clientNetworkManager.receiveMessage();

			networkMessage = chatmessage;
			break;
			
			
			
		case "rFile":
			
			/* 
			 * C ----------RECEIVER-----------> S
			 *   <--AD,Ks(F),K(ks)/NOK--FILE--
			 *   <-----------F----------------
			 *                          
			 */	

			clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(), parsedRequest.getPassword(),
					MessageType.RECEIVER);

			clientNetworkMessage.setContent(parsedRequest.getSpecificField());

			// enviar mensagem
			clientNetworkManager.sendMessage(clientNetworkMessage);

			// recebe Resposta
			chatmessage = (ChatMessage) clientNetworkManager.receiveMessage();
			networkMessage = chatmessage;
			
			break;
		
		case "-a":
			/* 
			 * C ----------ADDUSER-----------> S
			 *   <-------------OK/NOK--------                        
			 */	
			
			//preprara mensagem
			clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(), 
					parsedRequest.getPassword(), MessageType.ADDUSER);
			
			clientNetworkMessage.setContent(parsedRequest.getSpecificField());
			
			// enviar mensagem
			clientNetworkManager.sendMessage(clientNetworkMessage);
			
			// recebe Resposta
			chatmessage = (ChatMessage) clientNetworkManager.receiveMessage();
			networkMessage = chatmessage;
			
			break;
		case "-d":
			/* 
			 * C ----------ADDUSER-----------> S
			 *   <-------------OK/NOK--------                        
			 */	
			
			//preprara mensagem
			clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(), 
					parsedRequest.getPassword(), MessageType.REMOVEUSER);
			
			clientNetworkMessage.setContent(parsedRequest.getSpecificField());
			
			// enviar mensagem
			clientNetworkManager.sendMessage(clientNetworkMessage);
			
			// recebe Resposta
			chatmessage = (ChatMessage) clientNetworkManager.receiveMessage();
			networkMessage = chatmessage;
			
			break;	
			

		default:
			break;
		}

		return networkMessage;

	}

	/**
	 * @param type
	 * 
	 * 
	 */
	private void sendAuthenticationMessage() {

		ClientNetworkMessage aux_message = new ClientNetworkMessage(parsedRequest.getUsername(),
				parsedRequest.getPassword(), MessageType.AUTH);

		aux_message.setDestination(parsedRequest.getContact());
		System.out.println("Client - Enviar msg");
		clientNetworkManager.sendMessage(aux_message);

	}
}
