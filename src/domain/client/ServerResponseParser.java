package domain.client;

import java.io.IOException;

import network.managers.ClientNetworkManager;
import network.messages.ServerMessage;
import util.UserUtil;

/**
 * Classe que processa a resposta do servidor, ao pedidio inicial do cliente
 * 
 * @author Pedro, Jose, Antonio
 *
 */
public class ServerResponseParser {

	private UserUtil userUtil;
	private ClientNetworkManager clientNetwork;
	private String username;

	/**
	 * Construtor
	 * 
	 * @param userInterface
	 * @param clientNetwork
	 * @param username
	 */
	public ServerResponseParser(UserUtil userInterface, ClientNetworkManager clientNetwork, String username) {
		this.userUtil = userInterface;
		this.clientNetwork = clientNetwork;
		this.username = username;
	}

	/**
	 * Analisa e informa utilizador da resposta ao pedido efectuado
	 * @param serverMessage - Mensagem que o servido enviou
	 */
	public void ProcessMessage(ServerMessage serverMessage) {
		switch (serverMessage.getMessageType()) {
		// operacao bem sucedida
		case OK:
			userUtil.print("OK");
			break;
		// mensagem de erro
		case NOK:
			userUtil.print(serverMessage.getContent());
			break;

		// todas as mensagens de uma conversa
		case CONVERSATION:
			userUtil.printChatMessages(serverMessage.getMessageList(), username);
			break;

		// ultima mensagem de cada conversa em que o utilizador participou
		case LAST_MESSAGES:
			userUtil.printContactChatMessages(serverMessage.getMessageList(), username);
			break;

		// receber um ficheiro
		case FILE:
			try {
				clientNetwork.receiveFile(serverMessage.getFileSize(), serverMessage.getContent());
			} catch (IOException e) {
				userUtil.print("ERRO a receber o ficheiro");
				e.printStackTrace();
			}
			break;
		// mensagem do servidor mal formatada
		default:
			userUtil.print("Mensagem invalida");
			break;
		}
	}
}
