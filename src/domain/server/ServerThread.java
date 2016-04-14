package domain.server;

import java.net.Socket;

import network.managers.ServerNetworkManager;
import network.messages.ClientNetworkMessage;
import network.messages.NetworkMessage;

/**
 * <<THREAD>> Classe que executa o pedido de um cliente num novo fio de execução
 * 
 * @author Pedro, José, António
 *
 */
public class ServerThread extends Thread {

	private Socket socket;

	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	/**
	 * Executar thread
	 */
	public void run() {
		//incializa comunicação com o cliente e recebe ClientMessage
		ServerNetworkManager serverNetworkManager = new ServerNetworkManager(socket);
		ClientNetworkMessage clientMessage = (ClientNetworkMessage) serverNetworkManager.receiveMessage();

		// processa a mensagem do cliente e cria mensagem de resposta
		ServerClientMessageParser clientMessageParser = new ServerClientMessageParser(clientMessage, serverNetworkManager);
		NetworkMessage serverMessage = clientMessageParser.processRequest();

		// envia resposta ao cliente
		serverNetworkManager.sendMessage(serverMessage);
		serverNetworkManager.close();
	}
}
