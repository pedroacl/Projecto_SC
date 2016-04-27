package domain.client;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.util.Arrays;
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
	private KeyPair keyPair;

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

		System.out.println("[ClientRequestManager.processRequest] " + parsedRequest.getOrder());

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
					SecretKey sessionKey = SecurityUtils.generateSecretKey();
					System.out.println("[ClientRequestManager -m] SecretKey" + Base64.getEncoder().encodeToString(sessionKey.getEncoded()));

					// cifrar mensagem com chave de sessão
					byte[] encryptedMessage = SecurityUtils
							.cipherWithSessionKey(parsedRequest.getSpecificField().getBytes(), sessionKey);

					clientPGPMessage.setCypheredMessage(encryptedMessage);

					System.out.println("[ClientRequestManager] cypheredMessage: "
							+ MiscUtil.bytesToHex(clientPGPMessage.getCypheredMessage()));

					List<String> groupMembers = serverNetworkContactTypeMessage.getGroupMembers();

					// cifrar chave secreta, usada para cifrar mensagem anterior
					for (String groupMember : groupMembers) {
						// wrap da chave secreta a ser enviada com a chave
						// publica do contacto de destino
						byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(username, groupMember, userPassword,
								sessionKey);

						clientPGPMessage.putUserKey(groupMember, wrappedSecretKey);
					}

					// adicionar chave cifrada do proprio utilizador
					byte[] wrappedSecretKey = SecurityUtils.wrapSecretKey(username, username, userPassword, sessionKey);
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
			 * <------Contact/NOK-------
			 * ----AD,Ks(M),Kp(Ks)-----> 
			 * <---------OK/NOK------------
			 * -------Ks(file)-------------> 
			 * <--------OK/NOK-----------
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
					
					// contacto
					System.out.println("[ClientRequestManager] -f " + serverNetworkContactTypeMessage.numGroupMembers());

					// gerar assinatura
					byte[] clientSignature = SecurityUtils.signFile(parsedRequest.getSpecificField(),
							username);
					chatMessage.setSignature(new byte[5]); // TODO

					// obter chave secreta
					SecretKey secretKey = SecurityUtils.generateSecretKey();

					// envia tamanho do ficheiro
					chatMessage.setFileSize(parsedRequest.getFileSize());
					
					//envia nome do ficheiro
					// cifrar mensagem com chave de sessão
					byte[] encryptedMessage = SecurityUtils
							.cipherWithSessionKey(MiscUtil.extractName(parsedRequest.getSpecificField()).getBytes(), secretKey);
					
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
						clientNetworkManager.sendFile(parsedRequest.getSpecificField(), parsedRequest.getFileSize(), secretKey);

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

				System.out.println("-rLast");

				clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(),
						parsedRequest.getPassword(), MessageType.RECEIVER);

				clientNetworkMessage.setContent("recent");

				// enviar mensagem
				clientNetworkManager.sendMessage(clientNetworkMessage);

				// obter resposta
				ServerMessage serverMessage = (ServerMessage) clientNetworkManager.receiveMessage();
				List<ChatMessage> chatMessages = serverMessage.getMessageList();
				
				// iterar mensagens
				for (ChatMessage currChatMessage: chatMessages) {
					System.out.println("[ClientRequestManager] cipheredKey: "
							+ MiscUtil.bytesToHex(currChatMessage.getCypheredMessageKey()));
					
					// decifrar mensagem
					String decipheredMessage = SecurityUtils.decipherChatMessage(username, userPassword,
							currChatMessage.getCypheredMessageKey(),
							currChatMessage.getCypheredMessage());
					
					System.out.println("MENSAGEM DECIFRADA ->>>>> " + decipheredMessage);

					// validar assinatura
					byte[] signature = currChatMessage.getSignature();
					Certificate certificate = SecurityUtils.getCertificate(username, currChatMessage.getFromUser(), userPassword);

					try {
						SecurityUtils.verifySignature(decipheredMessage, certificate.getPublicKey(), signature);
					} catch (SignatureException e) {
						System.out.println("Assinatura invalida!");
						e.printStackTrace();
					}
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

				networkMessage = serverMessage2;
				break;

			case "-rFile":

				/*
				 * C ----------RECEIVER-----------> S
				 * <--AD,Ks(F),K(ks)/NOK--FILE--
				 *  <-----------F----------------
				 * ----------OK/NOK------------->
				 * <-----------OK/NOK-------------
				 */

				clientNetworkMessage = new ClientNetworkMessage(parsedRequest.getUsername(),
						parsedRequest.getPassword(), MessageType.RECEIVER);
				clientNetworkMessage.setDestination(parsedRequest.getContact());
				
				clientNetworkMessage.setContent(parsedRequest.getSpecificField());

				// enviar mensagem
				clientNetworkManager.sendMessage(clientNetworkMessage);

				// recebe Resposta
				ChatMessage chatmessage2 = (ChatMessage) clientNetworkManager.receiveMessage();
				
				//obtem chave de sessão para decifrar ficheiro
				SecretKey sessionKey = SecurityUtils.unwrapSessionKey
						(username, userPassword, chatmessage2.getCypheredMessageKey());
				
				//recebe ficheiro
			File newfile = null;
			try {
				newfile = clientNetworkManager.receiveFile(chatmessage2.getFileSize(), 
						chatmessage2.getContent(), sessionKey);
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
				//verifica se está tudo ok
				byte [] assinatura = SecurityUtils.signFile(newfile.getAbsolutePath(), username);
				
				boolean equalSign = Arrays.equals(assinatura, chatmessage2.getSignature());
				
				if(false) 	//TODO
					chatmessage2 = new ChatMessage (MessageType.OK);
				else 
					chatmessage2 = new ChatMessage (MessageType.NOK);
				
				// enviar mensagem
				clientNetworkManager.sendMessage(chatmessage2);
				
				//recebe resposta 
				networkMessage = (ServerMessage) clientNetworkManager.receiveMessage();
				if(networkMessage.getMessageType().equals(MessageType.NOK))
					networkMessage.setContent("Ficheiro corrompido");
				
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

				// enviar mensagem
				clientNetworkManager.sendMessage(clientNetworkMessage);

				// recebe Resposta
				ChatMessage chatmessage = (ChatMessage) clientNetworkManager.receiveMessage();
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

				// enviar mensagem
				clientNetworkManager.sendMessage(clientNetworkMessage);

				// recebe Resposta
				ChatMessage chatmessage3 = (ChatMessage) clientNetworkManager.receiveMessage();
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

		System.out.println(" [clientRequestManager] ENVIANDO MENSAGEM DE AUTH para " + parsedRequest.getContact());
		clientNetworkManager.sendMessage(aux_message);

	}
}
