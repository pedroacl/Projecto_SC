package domain.server;

import java.net.Socket;

import exceptions.InvalidMacException;
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

	private Socket	socket;

	private Authentication authentication;

	public ServerThread(Socket socket, Authentication authentication) {
		this.socket = socket;
		this.authentication = authentication;
	}

	/**
	 * Executar thread
	 */
	public void run() {
		// incializa comunicação com o cliente e recebe ClientMessage
		ServerNetworkManager serverNetworkManager = new ServerNetworkManager(socket);
		
		ClientNetworkMessage clientMessage = (ClientNetworkMessage) serverNetworkManager.receiveMessage();
		
		System.out.println("[SerThread] Recebi primeira mensagem. Type: "
		+ clientMessage.getMessageType() + " From: " + clientMessage.getUsername() );
		
		// processa a mensagem do cliente e cria mensagem de resposta
		ServerClientMessageParser clientMessageParser = new ServerClientMessageParser(clientMessage,
				serverNetworkManager, authentication);

		NetworkMessage  netMsg= clientMessageParser.processRequest();
		
		serverNetworkManager.sendMessage(netMsg);
	}
}
