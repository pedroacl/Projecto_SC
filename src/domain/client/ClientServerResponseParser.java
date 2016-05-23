package domain.client;

import java.util.ArrayList;

import network.messages.ChatMessage;
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

	public ClientServerResponseParser(ArgsParser argsParser) {
		// this.userUtil = userInterface;
		this.username = argsParser.getUsername();
	}

	/**
	 * Analisa e informa utilizador da resposta ao pedido efectuado
	 * 
	 * @param serverMessage
	 *            Mensagem que o servido enviou
	 * @requires serverMessage != null
	 */
	public void processMessage(NetworkMessage serverMessage) {
		
		
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
			UserUtil.printChatMessages(trueMessage.getMessageList(), username);
			
			break;

		// ultima mensagem de cada conversa em que o utilizador participou
		case LAST_MESSAGES:
			ArrayList<ChatMessage> chatMessages = ((ServerMessage) serverMessage).getMessageList();
			UserUtil.printContactChatMessages(((ServerMessage) serverMessage).getMessageList(), username);
			
			break;


		// mensagem do servidor mal formatada
		default:
			UserUtil.print("Mensagem invalida");
			break;
		}
	}
}
