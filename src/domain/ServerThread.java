package domain;

import java.io.IOException;
import java.net.Socket;

import interfaces.ServerThreadInterface;
import network.ClientMessage;
import network.ServerMessage;
import network.ServerSocketNetwork;
import parsers.ClientMessageParser;

/**
 * <<THREAD>> Classe que executa o pedido de um cliente num novo fio de execução
 * 
 * @author Pedro, José, Antonio
 *
 */
public class ServerThread extends Thread implements ServerThreadInterface {

	private Socket socket;

	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		ServerSocketNetwork serverSocketNetwork = new ServerSocketNetwork(socket);
		ClientMessage clientMessage = serverSocketNetwork.getClientMessage();

		// processa a mensagem do cliente e cria mensagem de resposta
		ClientMessageParser clientMessageParser = new ClientMessageParser(clientMessage, serverSocketNetwork);
		ServerMessage serverMessage = clientMessageParser.processRequest();

		// envia resposta ao cliente
		serverSocketNetwork.sendMessage(serverMessage);
		serverSocketNetwork.close();
	}
}
