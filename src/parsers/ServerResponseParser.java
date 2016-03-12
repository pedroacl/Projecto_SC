package parsers;

import java.io.IOException;

import network.ClientNetwork;
import network.ServerMessage;
import util.UserUtil;

public class ServerResponseParser {

	private UserUtil userUtil;
	private ClientNetwork clientNetwork;
	private String username;

	/**
	 * Construtor
	 * 
	 * @param userInterface
	 * @param clientNetwork
	 * @param username
	 */
	public ServerResponseParser(UserUtil userInterface, ClientNetwork clientNetwork, String username) {
		this.userUtil = userInterface;
		this.clientNetwork = clientNetwork;
		this.username = username;
	}

	/**
	 * 
	 * @param serverMessage
	 */
	public void ProcessMessage(ServerMessage serverMessage) {
		switch (serverMessage.getMessageType()) {
		// operacao bem sucedida
		case OK:
			userUtil.print("OK");
			break;
		// mensagem de erro
		case NOK:
			userUtil.print(serverMessage.getMessage());
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
				clientNetwork.receiveFile(serverMessage.getFileSize(), serverMessage.getMessage());
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
