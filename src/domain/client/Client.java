package domain.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import network.managers.ClientNetworkManager;
import network.messages.ClientNetworkMessage;
import network.messages.ClientPGPMessage;
import network.messages.MessageType;
import network.messages.ServerNetworkContactTypeMessage;
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

		// Cria mensagem de comunicaçao com o pedido do cliente
		Parsed parsedRequest = argsParser.getParsed();
		ClientRequestManager clientRequestManager = new ClientRequestManager(parsedRequest, clientNetwork);
		clientRequestManager.processRequest();

		// Verificar tipo de mensagem
		/*
		*/
		/*
		 * // envia a mensagem Boolean sended =
		 * clientNetwork.sendMessage(requestParsed);
		 * 
		 * if (sended) { // recebe a resposta ServerMessage serverMsg =
		 * (ServerMessage) clientNetwork.receiveMessage();
		 * 
		 * // passa resposta ao parser para ser processada ServerResponseParser
		 * srp = new ServerResponseParser(userInterface, clientNetwork,
		 * argsParser.getUsername());
		 * 
		 * srp.ProcessMessage(serverMsg);
		 * 
		 * } // fecha a ligaçao ao servidor clientNetwork.close();
		 */
	}
}
