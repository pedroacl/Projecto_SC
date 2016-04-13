package domain.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import network.managers.ClientNetworkManager;
import network.messages.ClientMessage;
import network.messages.ClientPGPMessage;
import network.messages.MessageType;
import network.messages.ServerContactTypeMessage;
import network.messages.ServerMessage;
import security.Security;
import util.UserUtil;

/**
 * Classe que representa um cliente, isto é responsavel por contactar o servidor
 * 
 * @author Pedro, José e Antonio
 *
 */
public class Client {

	private static ClientNetworkManager clientNetwork;

	/**
	 * Funçao principal
	 * 
	 * @param args
	 *            Argumentos com o pedido do utilizador
	 * 
	 */
	public static void main(String[] args) {
		ArgsParser argsParser = new ArgsParser(args);
		UserUtil userInterface = new UserUtil();

		// validar input
		if (!argsParser.validateInput()) {
			userInterface.printArgsUsage();
			System.exit(0);
		}

		// verifica se o utlizador preencheu password
		if (!argsParser.passwordFilled()) {
			argsParser.setPassword(userInterface.askForPassword());
		}

		// Cria Classe de comunicação entre Cliente e servidor
		int port = Integer.parseInt(argsParser.getServerPort());
		Socket socket = null;

		try {
			socket = new Socket(argsParser.getServerIP(), port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		clientNetwork = new ClientNetworkManager(socket);
		System.out.println("Cliente ligado ao servidor " + argsParser.getServerIP() + ":" + argsParser.getServerPort());

		// obter chave assimétrica do utilizador
		KeyPair keyPair = Security.getKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();

		// Cria mensagem de comunicaçao com o pedido do cliente
		Parsed requestParsed = argsParser.getParsed();

		// Verificar tipo de mensagem
		switch (requestParsed.getOrder()) {
		// client quer enviar uma mensagem
		case "-m":
			// enviar mensagem a perguntar o tipo do destinatario (contacto?
			// grupo?)
			ClientMessage aux_message = new ClientMessage(requestParsed.getUsername(), requestParsed.getPassword(),
					MessageType.MESSAGE);

			aux_message.setDestination(requestParsed.getContact());
			System.out.println("Client - Enviar msg");
			clientNetwork.sendMessage(aux_message);

			// obter tipo de contacto
			ServerContactTypeMessage serverContactTypeMessage = (ServerContactTypeMessage) clientNetwork
					.receiveMessage();

			System.out.println(serverContactTypeMessage.getMessageType());

			// existe contacto
			if (serverContactTypeMessage.getMessageType() == MessageType.CONTACT) {
				ClientPGPMessage clientPGPMessage = new ClientPGPMessage();

				// contacto
				if (serverContactTypeMessage.getGroupMembers().size() != 0) {
					System.out.println("Client - CONTACT");

					// gerar assinatura e enviar ao servidor
					byte[] clientSignature = Security.signMessage(requestParsed.getSpecificField(), privateKey);
					clientPGPMessage.setSignature(clientSignature);

					ArrayList<String> groupMembers = (ArrayList<String>) serverContactTypeMessage.getGroupMembers();

					// obter chave secreta
					SecretKey secretKey = Security.getSecretKey();
					
					// cifrar mensagem com chave secreta
					byte[] encryptedMessage = Security.cipherWithSecretKey(requestParsed.getSpecificField().getBytes(),
							secretKey);
					clientPGPMessage.setMessage(encryptedMessage);
					

					// cifrar chave privada, usada para cifrar mensagem anterior
					for (String username : groupMembers) {
						// wrap da chave secreta a ser enviada com a chave
						// publica do contacto de destino
						byte[] wrappedSecretKey = Security.wrapSecretKey(username, secretKey);	
						clientPGPMessage.putUserKey(username, wrappedSecretKey);
					}
				} else {

				}

				clientNetwork.sendMessage(clientPGPMessage);
			}

			// clientNetwork.sendMessage(aux_message);

			break;

		default:
			break;
		}

		/*
		// envia a mensagem
		Boolean sended = clientNetwork.sendMessage(requestParsed);
	
		if (sended) {
			// recebe a resposta
			ServerMessage serverMsg = (ServerMessage) clientNetwork.receiveMessage();

			// passa resposta ao parser para ser processada
			ServerResponseParser srp = new ServerResponseParser(userInterface, clientNetwork, argsParser.getUsername());

			srp.ProcessMessage(serverMsg);

		}
		// fecha a ligaçao ao servidor
		clientNetwork.close();
		*/
	}
}
