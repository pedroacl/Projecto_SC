package domain.client;

import java.security.KeyPair;
import java.util.Set;

import javax.crypto.SecretKey;

import network.managers.ClientNetworkManager;
import network.messages.ClientNetworkMessage;
import network.messages.ChatMessage;
import network.messages.MessageType;
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

	public void processRequest() {
		switch (parsedRequest.getOrder()) {
		// client quer enviar uma mensagem
		case "-m":
			// enviar mensagem a perguntar o tipo do destinatario (contacto?
			// grupo?)
			ClientNetworkMessage aux_message = new ClientNetworkMessage(parsedRequest.getUsername(),
					parsedRequest.getPassword(), MessageType.MESSAGE);

			aux_message.setDestination(parsedRequest.getContact());
			System.out.println("Client - Enviar msg");
			clientNetworkManager.sendMessage(aux_message);

			// obter tipo de contacto
			ServerNetworkContactTypeMessage serverNetworkContactTypeMessage = (ServerNetworkContactTypeMessage) clientNetworkManager
					.receiveMessage();

			// existe contacto
			if (serverNetworkContactTypeMessage.getMessageType() == MessageType.CONTACT) {
				ChatMessage clientPGPMessage = new ChatMessage(MessageType.PGP_MESSAGE);

				System.out.println(serverNetworkContactTypeMessage.numGroupMembers());

				// contacto
				System.out.println("Client - CONTACT");

				// gerar assinatura e enviar ao servidor
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

				// cifrar chave privada, usada para cifrar mensagem anterior
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
			} else {
				System.out.println("Nao ha");
			}

			// clientNetwork.sendMessage(aux_message);

			break;

		default:
			break;
		}

	}
}
