package domain;

import interfaces.ServerThreadInterface;
import network.ClientMessage;
import network.ServerMessage;
import network.ServerSocketNetwork;
import parsers.ClientMessageParser;
/**
 * <<THREAD>>
 * Classe que executa o pedido de um cliente num novo fio de execução
 * 
 * @author Pedro, José, Antonio
 *
 */
public class ServerThread extends Thread implements ServerThreadInterface {
	
	private ServerSocketNetwork serverSocketNetwork;
	
	public ServerThread(ServerSocketNetwork serverSocketNetwork) {
		this.serverSocketNetwork = serverSocketNetwork;
	}

	
	public void run() {
		//recebe Mensagem do cliente
		ClientMessage clientMessage = serverSocketNetwork.getClientMessage();
		
		//processa a mensagem do cliente e cria mensagem de resposta
		ClientMessageParser clientMessageParser = new ClientMessageParser(clientMessage, serverSocketNetwork);
		ServerMessage serverMessage = clientMessageParser.processRequest();
		
		//envia resposta ao cliente
		serverSocketNetwork.sendMessage(serverMessage);
	}
}
