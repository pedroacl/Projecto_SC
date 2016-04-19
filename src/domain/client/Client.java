package domain.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import network.managers.ClientNetworkManager;
import network.messages.NetworkMessage;
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
		
		System.setProperty("javax.net.ssl.trustStore", "truststore.cliente");
		System.setProperty("javax.net.ssl.trustStorePassword", "seguranca");
		
		SocketFactory sf = SSLSocketFactory.getDefault();
		Socket socket = null;

		try {
			socket = sf.createSocket(argsParser.getServerIP(), port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		clientNetwork = new ClientNetworkManager(socket);
		System.out.println("Cliente ligado ao servidor " + argsParser.getServerIP() + ":" + argsParser.getServerPort());

		// Cria mensagem de comunicaçao com o pedido do cliente
		Parsed parsedRequest = argsParser.getParsed();
		
		//Comunica com Servidor seguindo um protocolo dependendo do tipo de pedido do cliente
		ClientRequestManager clientRequestManager = new ClientRequestManager(parsedRequest, clientNetwork);
		
		//recebe resultado da comunicaçao
		NetworkMessage netWorkMessage = clientRequestManager.processRequest();
		
		ServerResponseParser srp = new ServerResponseParser(userInterface, argsParser.getUsername());
		srp.ProcessMessage(netWorkMessage);
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
