package domain.client;

import network.messages.NetworkMessage;
import network.messages.ServerMessage;
import util.UserUtil;

/**
 * Classe que processa a resposta do servidor, ao pedidio inicial do cliente
 * 
 * @author Pedro, José e António
 *
 */
public class ClientServerResponseParser {

	private String username;

	public ClientServerResponseParser(String username) {
		// this.userUtil = userInterface;
		this.username = username;
	}

	/**
	 * Analisa e informa utilizador da resposta ao pedido efectuado
	 * 
	 * @param serverMessage
	 *            Mensagem que o servido enviou
	 * @requires serverMessage != null
	 */
	public void processMessage(NetworkMessage serverMessage) {
		
		System.out.println("[ClientServerResponseParser] serverMessage: " + serverMessage);
		
		switch (serverMessage.getMessageType()) {
		// operacao bem sucedida
		case OK:
			UserUtil.print("OK");
			break;

		// mensagem de erro
		case NOK:
			UserUtil.print(serverMessage.getContent());
			break;

		// todas as mensagens de uma conversa
		case CONVERSATION:
			ServerMessage trueMessage = (ServerMessage) serverMessage;
			System.out.println("[ClientServerResponseParser] Lista: " + trueMessage.getMessageList());
			UserUtil.printChatMessages(trueMessage.getMessageList(), username);
			break;

		// ultima mensagem de cada conversa em que o utilizador participou
		case LAST_MESSAGES:
			System.out.println("[ClientServerResponseParser] LAST_MESSAGES");
			UserUtil.printContactChatMessages(((ServerMessage) serverMessage).getMessageList(), username);
			break;

		// receber um ficheiro
			/*
		case FILE:
			try {
				clientNetwork.receiveFile(serverMessage.getFileSize(), serverMessage.getContent());
			} catch (IOException e) {
				userUtil.print("ERRO a receber o ficheiro");
				e.printStackTrace();
			}
			break;
			*/

		// mensagem do servidor mal formatada
		default:
			UserUtil.print("Mensagem invalida");
			break;
		}
	}
}