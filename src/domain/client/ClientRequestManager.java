package domain.client;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.crypto.SecretKey;

import exceptions.AliasNotFoundException;
import network.managers.ClientNetworkManager;
import network.messages.ChatMessage;
import network.messages.ClientNetworkMessage;
import network.messages.MessageType;
import network.messages.NetworkMessage;
import network.messages.ServerNetworkContactTypeMessage;
import security.SecurityUtils;
import util.MiscUtil;

public class ClientRequestManager {

	private Parsed					parsedRequest;
	private ClientNetworkManager	clientNetworkManager;
	private KeyPair					keyPair;

	public ClientRequestManager(Parsed parsedRequest, ClientNetworkManager clientNetworkManager) {
		this.parsedRequest = parsedRequest;
		this.clientNetworkManager = clientNetworkManager;

		// obter chave assimétrica do utilizador
		keyPair = SecurityUtils.getKeyPair();
	}

	public NetworkMessage processRequest() throws AliasNotFoundException, KeyStoreException, IOException {

		NetworkMessage networkMessage = null;
		ServerNetworkContactTypeMessage serverNetworkContactTypeMessage = null;
		ChatMessage chatMessage = null;
		ClientNetworkMessage clientNetworkMessage;

		switch (parsedRequest.getOrder()) {
		// client quer enviar uma mensagem

		/*
		 * C --------AUTH------------> S <------Contact/NOK-------
		 * (ServerNetworkContactTypeMessage) ----AD,Ks(M),Kp(Ks)----->
		 * (ChatMessage) <-------OK/NOK----------
		 */

		case "-m":
			// enviar mensagem a perguntar o tipo do destinatario (contacto?
			// grupo?)
			sendAuthenticationMessage(MessageType.MESSAGE);

			// obter tipo de contacto
			serverNetworkContactTypeMessage = (ServerNetworkContactTypeMessage) clientNetworkManager.receiveMessage();

			// existe contacto
			if (serverNetworkContactTypeMessage.getMessageType() == MessageType.CONTACT) {
				ChatMessage clientPGPMessage = new ChatMessage(MessageType.MESSAGE);
				
				clientPGPMessage.setFromUser(parsedRequest.getUsername());
				clientPGPMessage.setDestination(parsedRequest.getContact());

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

				clientPGPMessage.setCypheredMessage(encryptedMessage);
				System.out.println("[ClientRequestManager] cypheredMessage: " + clientPGPMessage.getContent());
				List<String> groupMembers = serverNetworkContactTypeMessage.getGroupMembers();

				// cifrar chave secreta, usada para cifrar mensagem anterior
				for (String username : groupMembers) {
					// wrap da chave secreta a ser enviada com a chave
					// publica do contacto de destino
					Certificate certificate = SecurityUtils.getCertificate(username);
					byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(secretKey, certificate);

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
		 * C --------AUTH------------> S <------Contact/NOK-------
		 * ----AD,Ks(M),Kp(Ks)-----> <---------OK/NOK------------
		 * -------file-------------> <--------OK/NOK----------
		 */

		case "-f":
			// enviar mensagem a perguntar o tipo do destinatario (user? grupo?)
			sendAuthenticationMessage(MessageType.FILE);

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

				List<String> groupMembers = (List<String>) serverNetworkContactTypeMessage.getGroupMembers();

				// cifrar chave secreta, usada para cifrar ficheiro
				for (String username : groupMembers) {
					// obter certificado
					Certificate certificate = SecurityUtils.getCertificate(username);
					
					if (certificate == null) {
						throw new AliasNotFoundException("Não existe alias para o user " + username);
					}

					// wrap da chave secreta a ser enviada com a chave
					// publica do contacto de destino
					byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(secretKey, certificate);
					chatMessage.putUserKey(username, wrappedSecretKey);
				}

				// adicionar chave cifrada do proprio utilizador
				byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(secretKey,
						SecurityUtils.getCertificate(parsedRequest.getUsername()));

				chatMessage.putUserKey(parsedRequest.getUsername(), wrappedSecretKey);

				// enviar mensagem
				clientNetworkManager.sendMessage(chatMessage);

				// espera resposta OK or NOK
				networkMessage = (NetworkMessage) clientNetworkManager.receiveMessage();

				if (networkMessage.getMessageType().equals(MessageType.OK)) {
					// enviar ficheiro
					clientNetworkManager.sendFile(chatMessage, secretKey);

					// esperar resposta confimação
					networkMessage = (NetworkMessage) clientNetworkManager.receiveMessage();
				}

			} else {

				// nao existe contact, devolve erro
				networkMessage = serverNetworkContactTypeMessage;
			}
			break;

		/*
		 * C ----------RECEIVER-----------> S <--AD,Ks(M),K(k), M/NOK--LAST--
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
			 * <--AD,Ks(M),K(k),M/NOK--CONVERSATION--
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
			 * C ----------RECEIVER-----------> S <--AD,Ks(F),K(ks)/NOK--FILE--
			 * <-----------F---------------- ----------OK/NOK------------->
			 * <-----------OK/NOK-------------
			 */

			clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(), parsedRequest.getPassword(),
					MessageType.RECEIVER);

			clientNetworkMessage.setContent(parsedRequest.getSpecificField());

			// enviar mensagem
			clientNetworkManager.sendMessage(clientNetworkMessage);

			// recebe Resposta
			chatmessage = (ChatMessage) clientNetworkManager.receiveMessage();

			// recebe file
			// TODO
			// clientNetworkManeger.receiveFile(chatmessage,
			// chatmessage.getSecretKey)
			// Sign File e compara com AD

			break;

		case "-a":
			/*
			 * C ----------ADDUSER-----------> S <-------------OK/NOK--------
			 */

			// preprara mensagem
			clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(), parsedRequest.getPassword(),
					MessageType.ADDUSER);

			clientNetworkMessage.setContent(parsedRequest.getSpecificField());

			// enviar mensagem
			clientNetworkManager.sendMessage(clientNetworkMessage);

			// recebe Resposta
			chatmessage = (ChatMessage) clientNetworkManager.receiveMessage();
			networkMessage = chatmessage;

			break;
		case "-d":
			/*
			 * C ----------ADDUSER-----------> S <-------------OK/NOK--------
			 */

			// preprara mensagem
			clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(), parsedRequest.getPassword(),
					MessageType.REMOVEUSER);

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
	private void sendAuthenticationMessage(MessageType messageType) {

		ClientNetworkMessage aux_message = new ClientNetworkMessage(parsedRequest.getUsername(),
				parsedRequest.getPassword(), messageType);

		aux_message.setDestination(parsedRequest.getContact());

		System.out.println(" [clientRequestManager] ENVIANDO MENSAGEM DE AUTH para " + parsedRequest.getContact());
		clientNetworkManager.sendMessage(aux_message);

	}
}
