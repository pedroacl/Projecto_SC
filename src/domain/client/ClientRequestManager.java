package domain.client;

import java.security.KeyPair;
import java.util.Set;

import javax.crypto.SecretKey;

import network.managers.ClientNetworkManager;
import network.messages.ClientNetworkMessage;
import network.messages.ChatMessage;
import network.messages.MessageType;
import network.messages.NetworkMessage;
import network.messages.ServerMessage;
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
		
		switch (parsedRequest.getOrder()) {
		// client quer enviar uma mensagem
		case "-m":
			// enviar mensagem a perguntar o tipo do destinatario (contacto?
			// grupo?)
			sendAuthenticationMessage();
			
			// obter tipo de contacto
			ServerNetworkContactTypeMessage serverNetworkContactTypeMessage = (ServerNetworkContactTypeMessage) clientNetworkManager
					.receiveMessage();

			// existe contacto
			if (serverNetworkContactTypeMessage.getMessageType() == MessageType.CONTACT) {
				ChatMessage clientPGPMessage = new ChatMessage(MessageType.MESSAGE);

				System.out.println(serverNetworkContactTypeMessage.numGroupMembers());

				// contacto
				System.out.println("Client - CONTACT");

				// gerar assinatura
				byte[] clientSignature = SecurityUtils.signMessage(parsedRequest.getSpecificField(), keyPair.getPrivate());
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
				
				//espera resposta
				 networkMessage = (NetworkMessage) clientNetworkManager.receiveMessage();
				
			} else {
				//nao existe contact, devolve erro
				networkMessage = serverNetworkContactTypeMessage;
				
			}
			break;
			
		case "-f":
			// enviar mensagem a perguntar o tipo do destinatario (user? grupo?)
			sendAuthenticationMessage();
			
			// obter tipo de contacto
			ServerNetworkContactTypeMessage serverNetworkContactTypeMessage = 
					(ServerNetworkContactTypeMessage) clientNetworkManager.receiveMessage();
			
			// existe contacto
			if (serverNetworkContactTypeMessage.getMessageType() == MessageType.CONTACT) {
				
				ChatMessage clientPGPMessage = new ChatMessage(MessageType.FILE);

				System.out.println(serverNetworkContactTypeMessage.numGroupMembers());

				// contacto
				System.out.println("Client - CONTACT");
				
				// gerar assinatura
				byte[] clientSignature = SecurityUtils.signFile(parsedRequest.getSpecificField(), keyPair.getPrivate());
				clientPGPMessage.setSignature(clientSignature);

				// obter chave secreta
				SecretKey secretKey = SecurityUtils.generateSecretKey();

				// envia tamanho do ficheiro
				clientPGPMessage.setFileSize(parsedRequest.getFileSize());

				Set<String> groupMembers = (Set<String>) serverNetworkContactTypeMessage.getGroupMembers();

				// cifrar chave secreta, usada para cifrar ficheiro
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
				
				//espera resposta
				 networkMessage = (NetworkMessage) clientNetworkManager.receiveMessage();
				 
			} else {
				
				//nao existe contact, devolve erro
				networkMessage = serverNetworkContactTypeMessage;	
			}
			break;
			
		case "rLast":
			
			ClientNetworkMessage aux_message = new ClientNetworkMessage(parsedRequest.getUsername(),
					parsedRequest.getPassword(),MessageType.RECEIVER);
			
			aux_message.setContent("recent");
			
			// enviar mensagem
			clientNetworkManager.sendMessage(aux_message);
			
			//espera resposta
			ChatMessage chatmessage = (ChatMessage) clientNetworkManager.receiveMessage();
			
			networkMessage = chatmessage;
			break;
			
		case "rContact":
			
			ClientNetworkMessage aux_message = new ClientNetworkMessage(parsedRequest.getUsername(),
					parsedRequest.getPassword(),MessageType.RECEIVER);
			
			aux_message.setContent("all");
			
			// enviar mensagem
			clientNetworkManager.sendMessage(aux_message);
			
			//espera resposta
			ChatMessage chatmessage = (ChatMessage) clientNetworkManager.receiveMessage();
			
			networkMessage = chatmessage;
			break;
			
		case "rFile":
			
			ClientNetworkMessage aux_message = new ClientNetworkMessage(parsedRequest.getUsername(),
					parsedRequest.getPassword(),MessageType.RECEIVER);
			
			aux_message.setContent(parsedRequest.getSpecificField());
			
			// enviar mensagem
			clientNetworkManager.sendMessage(aux_message);
			
			//recebe Resposta
			ChatMessage chatmessage = (ChatMessage) clientNetworkManager.receiveMessage();
			
			
			
		

		default:
			break;
		}
		
		return networkMessage;

	}
	
	/**
	 * @param type TODO
	 *  
	 */
	private void sendAuthenticationMessage() {
		
		ClientNetworkMessage aux_message = new ClientNetworkMessage(parsedRequest.getUsername(),
							parsedRequest.getPassword(),MessageType.AUTH);

		aux_message.setDestination(parsedRequest.getContact());
		System.out.println("Client - Enviar msg");
		clientNetworkManager.sendMessage(aux_message);
	
	}
}
