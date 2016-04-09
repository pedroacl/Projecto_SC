package domain.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import network.managers.ClientNetworkManager;
import network.messages.ClientMessage;
import network.messages.MessageType;
import network.messages.ServerMessage;
import security.ClientSecurity;
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

		// gerar chave assimétrica
		ClientSecurity clientSecurity = new ClientSecurity();
		KeyPair keyPair = clientSecurity.getKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();

		// Cria mensagem de comunicaçao com o pedido do cliente
		ClientMessage clientMessage = argsParser.getMessage();

		// Verificar tipo de mensagem
		switch (clientMessage.getMessageType()) {
		// client quer enviar uma mensagem
		case MESSAGE:
			// enviar mensagem a perguntar o tipo do destinatario (contacto? grupo?)
			ClientMessage aux_message = new ClientMessage(clientMessage.getUsername(), clientMessage.getPassword(),
					MessageType.CONTACT_TYPE);

			clientNetwork.sendMessage(aux_message);
			ServerMessage serverMsg = (ServerMessage) clientNetwork.receiveMessage();
			
			// obter key publica do utilizador
			Key myKey = new SecretKeySpec(serverMsg.getContent().getBytes() , "AES");

			// gerar assinatura e enviar ao servidor
			byte[] clientSignature = clientSecurity.signMessage(clientMessage.getContent(), privateKey);
			aux_message.setContent(clientSignature.toString());
			clientNetwork.sendMessage(aux_message);

			// cifrar mensagem com chave secreta e enviar ao servidor
			SecretKey secretKey = clientSecurity.getSecretKey();
			byte[] encryptedMessage = clientSecurity.cipherWithSecretKey(clientMessage.getContent().getBytes(), secretKey);
			aux_message.setContent(encryptedMessage.toString());
			clientNetwork.sendMessage(aux_message);
			
			// cifrar chave privada, usada para cifrar mensagem anterior e enviar ao servidor
			byte[] wrappedSecretKey = clientSecurity.wrapSecretKey(secretKey);
			aux_message.setContent(wrappedSecretKey.toString());
			clientNetwork.sendMessage(aux_message);

			break;

		default:
			break;
		}

		// envia a mensagem
		Boolean sended = clientNetwork.sendMessage(clientMessage);

		if (sended) {
			// recebe a resposta
			ServerMessage serverMsg = (ServerMessage) clientNetwork.receiveMessage();

			// passa resposta ao parser para ser processada
			ServerResponseParser srp = new ServerResponseParser(userInterface, clientNetwork, argsParser.getUsername());

			srp.ProcessMessage(serverMsg);

		}
		// fecha a ligaçao ao servidor
		clientNetwork.close();

	}
}
