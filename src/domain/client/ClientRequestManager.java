package domain.client;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;

import exceptions.AliasNotFoundException;
import network.managers.ClientNetworkManager;
import network.messages.ChatMessage;
import network.messages.ClientNetworkMessage;
import network.messages.MessageType;
import network.messages.NetworkMessage;
import network.messages.ServerMessage;
import network.messages.ServerNetworkContactTypeMessage;
import util.MiscUtil;
import util.SecurityUtils;

public class ClientRequestManager {

	private Parsed parsedRequest;
	private ClientNetworkManager clientNetworkManager;

	public ClientRequestManager(Parsed parsedRequest, ClientNetworkManager clientNetworkManager) {
		this.parsedRequest = parsedRequest;
		this.clientNetworkManager = clientNetworkManager;

	}

	public NetworkMessage processRequest() throws AliasNotFoundException, KeyStoreException, IOException {

		NetworkMessage networkMessage = null;
		ServerNetworkContactTypeMessage serverNetworkContactTypeMessage = null;
		ChatMessage chatMessage = null;
		ClientNetworkMessage clientNetworkMessage;

		String username = parsedRequest.getUsername();
		String userPassword = parsedRequest.getPassword();

		switch (parsedRequest.getOrder()) {
			// client quer enviar uma mensagem

			/*
			 * C --------AUTH------------> S <------Contact/NOK-------
			 * (ServerNetworkContactTypeMessage) ----AD,Ks(M),Kp(Ks)----->
			 * (ChatMessage) <-------OK/NOK----------
			 */

			/**
			 * Enviar mensagem a um utilizador
			 */
			case "-m":
				// enviar mensagem a perguntar o tipo do destinatario (contacto?
				// grupo?)
				sendAuthenticationMessage(MessageType.MESSAGE);

				// obter tipo de contacto
				serverNetworkContactTypeMessage = (ServerNetworkContactTypeMessage) clientNetworkManager
						.receiveMessage();

				// existe contacto
				if (serverNetworkContactTypeMessage.getMessageType() == MessageType.CONTACT) {
					ChatMessage clientChatMessage = new ChatMessage(MessageType.MESSAGE);

					clientChatMessage.setFromUser(parsedRequest.getUsername());
					clientChatMessage.setDestination(parsedRequest.getContact());


					// contacto
					System.out.println("Client - CONTACT");

					// gerar assinatura
					byte[] clientSignature = null;
					try {
						clientSignature = SecurityUtils.signMessage(parsedRequest.getSpecificField(),
								SecurityUtils.getPrivateKey(username, userPassword));
					} catch (Exception e) {
						e.printStackTrace();
					}

					clientChatMessage.setSignature(clientSignature);

					// obter chave secreta
					SecretKey sessionKey = SecurityUtils.generateSecretKey();
					

					// cifrar mensagem com chave de sessão
					byte[] encryptedMessage = SecurityUtils
							.cipherWithSessionKey(parsedRequest.getSpecificField().getBytes(), sessionKey);

					clientChatMessage.setCypheredMessage(encryptedMessage);

					List<String> groupMembers = serverNetworkContactTypeMessage.getGroupMembers();

					// cifrar chave secreta, usada para cifrar mensagem anterior
					for (String groupMember : groupMembers) {
						// wrap da chave secreta a ser enviada com a chave
						// publica do contacto de destino
						byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(username, groupMember, userPassword,
								sessionKey);

						clientChatMessage.putUserKey(groupMember, wrappedSecretKey);
					}

					// adicionar chave cifrada do proprio utilizador
					byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(username, username, userPassword, sessionKey);
					clientChatMessage.putUserKey(parsedRequest.getUsername(), wrappedSecretKey);

					// enviar mensagem
					clientNetworkManager.sendMessage(clientChatMessage);

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
			 * -------Ks(file)-------------> <--------OK/NOK-----------
			 */

			/**
			 * Enviar um ficheiro a um utilizador
			 */
			case "-f":
				// enviar mensagem a perguntar o tipo do destinatario (user?
				// grupo?)
				sendAuthenticationMessage(MessageType.FILE);

				// obter tipo de contacto
				serverNetworkContactTypeMessage = (ServerNetworkContactTypeMessage) clientNetworkManager
						.receiveMessage();

				// existe contacto
				if (serverNetworkContactTypeMessage.getMessageType() == MessageType.CONTACT) {

					chatMessage = new ChatMessage(MessageType.FILE);
					chatMessage.setDestination(parsedRequest.getContact());
					chatMessage.setFromUser(username);

					// obtem chave privada do utilizador
					PrivateKey privateKey;
					byte[] clientSignature = null;
					try {
						privateKey = SecurityUtils.getPrivateKey(username, userPassword);
						// gerar assinatura
						clientSignature = SecurityUtils.signFile(parsedRequest.getSpecificField(), privateKey);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					chatMessage.setSignature(clientSignature);

					// obter chave secreta
					SecretKey secretKey = SecurityUtils.generateSecretKey();

					// envia tamanho do ficheiro
					chatMessage.setFileSize(parsedRequest.getFileSize());

					// envia nome do ficheiro
					// cifrar mensagem com chave de sessão
					byte[] encryptedMessage = SecurityUtils.cipherWithSessionKey(
							MiscUtil.extractName(parsedRequest.getSpecificField()).getBytes(), secretKey);

					chatMessage.setCypheredMessage(encryptedMessage);
					chatMessage.setContent(MiscUtil.extractName(parsedRequest.getSpecificField()));

					List<String> groupMembers = (List<String>) serverNetworkContactTypeMessage.getGroupMembers();

					// cifrar chave secreta, usada para cifrar ficheiro
					for (String groupMember : groupMembers) {
						// wrap da chave secreta a ser enviada com a chave
						// publica do contacto de destino
						byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(username, groupMember, userPassword,
								secretKey);
						chatMessage.putUserKey(groupMember, wrappedSecretKey);
					}

					// adicionar chave cifrada do proprio utilizador
					byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(username, username, userPassword, secretKey);

					chatMessage.putUserKey(parsedRequest.getUsername(), wrappedSecretKey);

					// enviar mensagem
					clientNetworkManager.sendMessage(chatMessage);

					// espera resposta OK or NOK
					networkMessage = (NetworkMessage) clientNetworkManager.receiveMessage();

					if (networkMessage.getMessageType().equals(MessageType.OK)) {
						// enviar ficheiro
						clientNetworkManager.sendFile(parsedRequest.getSpecificField(), parsedRequest.getFileSize(),
								secretKey);

						// esperar resposta confimação
						networkMessage = (NetworkMessage) clientNetworkManager.receiveMessage();
					}

				} else {

					// nao existe contact, devolve erro
					networkMessage = serverNetworkContactTypeMessage;
				}
				break;

			/*
			 * C ----------RECEIVER-----------> S <--AD,Ks(M),K(k),
			 * M/NOK--LAST--
			 */

			/**
			 * Receber as ultimas mensagens trocadas com cada utilizador
			 */
			case "-rLast":


				clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(),
						parsedRequest.getPassword(), MessageType.RECEIVER);

				clientNetworkMessage.setContent("recent");

				// enviar mensagem
				clientNetworkManager.sendMessage(clientNetworkMessage);

				// obter resposta
				ServerMessage serverMessage = (ServerMessage) clientNetworkManager.receiveMessage();
				List<ChatMessage> chatMessages = serverMessage.getMessageList();

				// iterar mensagens
				for (ChatMessage currChatMessage : chatMessages) {

					// decifrar mensagem
					String decipheredMessage = SecurityUtils.decipherChatMessage(username, userPassword,
							currChatMessage.getCypheredMessageKey(), currChatMessage.getCypheredMessage());

					//só para as mesnagens de texto.
					boolean isValid = true;
					if(currChatMessage.getMessageType().equals(MessageType.MESSAGE)) {
					
						// validar assinatura
						byte[] signature = currChatMessage.getSignature();
						Certificate certificate = SecurityUtils.getCertificate(username, currChatMessage.getFromUser(),
								userPassword);
						
						
						try {
							isValid = SecurityUtils.verifyMessageSignature(decipheredMessage, certificate.getPublicKey(), signature);
						} catch (SignatureException e) {
							e.printStackTrace();
						}
						
					}
					if(!isValid)
						currChatMessage.setContent("ALERTA: Mensagem Corrompida");
					else
						currChatMessage.setContent(decipheredMessage);
						
				}
				networkMessage = serverMessage;

				break;

			case "-rContact":

				/*
				 * C -------------RECEIVER--------------> S
				 * <--AD,Ks(M),K(k),M/NOK--CONVERSATION--
				 */

				clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(),
						parsedRequest.getPassword(), MessageType.RECEIVER);

				clientNetworkMessage.setDestination(parsedRequest.getContact());
				clientNetworkMessage.setContent("all");

				// enviar mensagem
				clientNetworkManager.sendMessage(clientNetworkMessage);

				// espera resposta
				ServerMessage serverMessage2 = (ServerMessage) clientNetworkManager.receiveMessage();
				List<ChatMessage> chatMessages2 = serverMessage2.getMessageList();

				// iterar mensagens
				for (ChatMessage currChatMessage : chatMessages2) {

					// decifrar mensagem
					String decipheredMessage = SecurityUtils.decipherChatMessage(username, userPassword,
							currChatMessage.getCypheredMessageKey(), currChatMessage.getCypheredMessage());
					
			
					//só para as mesnagens de texto.
					boolean isValid = true;
					if(currChatMessage.getMessageType().equals(MessageType.MESSAGE)) {
					
						// validar assinatura
						byte[] signature = currChatMessage.getSignature();
						Certificate certificate = SecurityUtils.getCertificate(username, currChatMessage.getFromUser(),
								userPassword);
						
						
						try {
							isValid = SecurityUtils.verifyMessageSignature(decipheredMessage, certificate.getPublicKey(), signature);
						} catch (SignatureException e) {
							e.printStackTrace();
						}
						
					
					}
					if(!isValid)
						currChatMessage.setContent("ALERTA: Mensagem Corrompida");
					else
						currChatMessage.setContent(decipheredMessage);
						
				}

				networkMessage = serverMessage2;
				break;

			/**
			 * Receber ficheiro enviado por outro utilizador
			 */
			case "-rFile":
				/*
				 * C ----------RECEIVER-----------> S
				 * <--AD,Ks(F),K(ks)/NOK--FILE-- 
				 * <-----------F----------------
				 * ----------OK/NOK------------->
				 * <-----------OK/NOK-------------
				 */

				System.out.println("-rFile");

				clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(),
						parsedRequest.getPassword(), MessageType.RECEIVER);
				clientNetworkMessage.setDestination(parsedRequest.getContact());

				clientNetworkMessage.setContent(parsedRequest.getSpecificField());

				// enviar mensagem
				clientNetworkManager.sendMessage(clientNetworkMessage);

				// recebe Resposta
				chatMessage = (ChatMessage) clientNetworkManager.receiveMessage();
				
				if(!chatMessage.getMessageType().equals(MessageType.NOK)) {

					// obtem chave de sessão para decifrar ficheiro
					SecretKey sessionKey = SecurityUtils.unwrapSessionKey(username, userPassword,
							chatMessage.getCypheredMessageKey());
	
					// recebe ficheiro
					File newfile = null;
					try {
						newfile = clientNetworkManager.receiveFile(chatMessage.getFileSize(), chatMessage.getContent(),
								sessionKey);
					} catch (GeneralSecurityException e) {
						e.printStackTrace();
					}
	
					//obtem chave publica
					Certificate cert = SecurityUtils.getCertificate(username, chatMessage.getFromUser(), userPassword);
					PublicKey publicKey = cert.getPublicKey();
		
					if (!SecurityUtils.verifyFileSignature(newfile.getAbsolutePath(), publicKey,
							chatMessage.getSignature())) {
						chatMessage = new ChatMessage(MessageType.NOK);
						System.out.println("Assinatura do ficheiro invalida!");
					}
					else
						chatMessage = new ChatMessage(MessageType.OK);
		
						// enviar mensagem
						clientNetworkManager.sendMessage(chatMessage);
		
						// recebe resposta
						networkMessage = (ServerMessage) clientNetworkManager.receiveMessage();
						if (networkMessage.getMessageType().equals(MessageType.NOK))
							networkMessage.setContent("Ficheiro corrompido");
				}
				
				else {
					networkMessage = new NetworkMessage(chatMessage.getMessageType());
					networkMessage.setContent(chatMessage.getContent());
				}
				break;

			case "-a":
				/*
				 * C ----------ADDUSER-----------> S
				 * <-------------OK/NOK--------
				 */

				// preprara mensagem
				clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(),
						parsedRequest.getPassword(), MessageType.ADDUSER);

				clientNetworkMessage.setContent(parsedRequest.getSpecificField());
				clientNetworkMessage.setDestination(parsedRequest.getContact());

				// enviar mensagem
				clientNetworkManager.sendMessage(clientNetworkMessage);

				// recebe Resposta
				ServerMessage chatmessage = (ServerMessage) clientNetworkManager.receiveMessage();

				networkMessage = chatmessage;

				break;
			case "-d":
				/*
				 * C ----------ADDUSER-----------> S
				 * <-------------OK/NOK--------
				 */

				// preprara mensagem
				clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(),
						parsedRequest.getPassword(), MessageType.REMOVEUSER);

				clientNetworkMessage.setContent(parsedRequest.getSpecificField());
				clientNetworkMessage.setDestination(parsedRequest.getContact());

				// enviar mensagem
				clientNetworkManager.sendMessage(clientNetworkMessage);

				// recebe Resposta
				ServerMessage chatmessage3 = (ServerMessage) clientNetworkManager.receiveMessage();
				networkMessage = chatmessage3;

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

		
		clientNetworkManager.sendMessage(aux_message);

	}
}
