package domain;

import java.net.Socket;

import network.ClientMessage;
import network.ServerMessage;
import network.ServerNetworkManager;
import parsers.ClientMessageParser;

/**
 * <<THREAD>> Classe que executa o pedido de um cliente num novo fio de execução
 * 
 * @author Pedro, José, Antonio
 *
 */
public class ServerThread extends Thread {

	private Socket socket;

	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		ServerNetworkManager serverNetworkManager = new ServerNetworkManager(socket);
		ClientMessage clientMessage = (ClientMessage) serverNetworkManager.receiveMessage();

		// processa a mensagem do cliente e cria mensagem de resposta
		ClientMessageParser clientMessageParser = new ClientMessageParser(clientMessage, serverNetworkManager);
		ServerMessage serverMessage = clientMessageParser.processRequest();

		// envia resposta ao cliente
		serverNetworkManager.sendMessage(serverMessage);
		serverNetworkManager.close();
	}
}
