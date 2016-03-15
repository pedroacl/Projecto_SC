package domain.server;

import java.net.Socket;

import network.managers.ServerNetworkManager;
import network.messages.ClientMessage;
import network.messages.ServerMessage;

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
		//incializa comunicação com o cliente e recebe ClientMessage
		ServerNetworkManager serverNetworkManager = new ServerNetworkManager(socket);
		ClientMessage clientMessage = (ClientMessage) serverNetworkManager.receiveMessage();
		System.out.println("!!!!" + clientMessage);

		// processa a mensagem do cliente e cria mensagem de resposta
		ClientMessageParser clientMessageParser = new ClientMessageParser(clientMessage, serverNetworkManager);
		ServerMessage serverMessage = clientMessageParser.processRequest();

		// envia resposta ao cliente
		serverNetworkManager.sendMessage(serverMessage);
		serverNetworkManager.close();
	}
}
