package domain.server;

import java.net.Socket;

import exceptions.InvalidMacException;
import network.managers.ServerNetworkManager;
import network.messages.ClientNetworkMessage;

/**
 * <<THREAD>> Classe que executa o pedido de um cliente num novo fio de execução
 * 
 * @author Pedro, José, António
 *
 */
public class ServerThread extends Thread {

	private Socket	socket;

	private String	password;

	public ServerThread(Socket socket, String password) {
		this.socket = socket;
		this.password = password;
	}

	/**
	 * Executar thread
	 */
	public void run() {
		// incializa comunicação com o cliente e recebe ClientMessage
		ServerNetworkManager serverNetworkManager = new ServerNetworkManager(socket);
		ClientNetworkMessage clientMessage = (ClientNetworkMessage) serverNetworkManager.receiveMessage();

		// processa a mensagem do cliente e cria mensagem de resposta
		ServerClientMessageParser clientMessageParser = new ServerClientMessageParser(clientMessage,
				serverNetworkManager, this.password);

		try {
			clientMessageParser.processRequest();
		} catch (InvalidMacException e) {
			e.printStackTrace();
		}
	}
}
