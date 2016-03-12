package domain;
import network.ClientMessage;
import network.ClientNetwork;
import network.ServerMessage;
import parsers.ArgsParser;
import parsers.ServerResponseParser;
import util.UserUtil;
/**
 * Classe que representa um cliente, isto é responsavel por contactar o servidor
 * 
 * @author Pedro, José, Antonio
 *
 */

public class Client{
	
	private static ClientNetwork clientNetwork;
	
	/**
	 * Funçao principal
	 * @param args - argumentos com o pedido do utilizador
	 * 
	 */
	public static void main(String[] args) {
		System.out.println("argsLength = " + args.length );

		for(String s : args) {
			System.out.println(s);
		}
		
		ArgsParser argsParser = new ArgsParser(args);
		UserUtil userInterface = new UserUtil();
		
		//validar input
		if(!argsParser.validateInput()) {
			userInterface.printArgsUsage();
			System.exit(0);
		}
		
		//Cria Classe de comunicação entre Cliente e servidor
		clientNetwork = new ClientNetwork(argsParser.getServerIP(), argsParser.getServerPort());
		clientNetwork.connect();
		
		System.out.println("Cliente ligado ao servidor " + argsParser.getServerIP() + ":" + argsParser.getServerPort());
	
		//Cria mensagem de comunicaçao com o pedido do cliente
		ClientMessage clientMessage = argsParser.getMessage();
		System.out.println("[Client.java] message: " + clientMessage);
		
		//envia a mensagem
		Boolean sended = clientNetwork.sendMessage(clientMessage);

		
		if(sended) {
			//recebe a resposta
			ServerMessage serverMsg = clientNetwork.receiveMessage();
		
			//para debbug
			System.out.println("[Client] Recebeu ultima Resposta: " + serverMsg.getMessageType());
			
			//passa resposta ao parser para ser processada
			ServerResponseParser srp = new ServerResponseParser(userInterface, clientNetwork, argsParser.getUsername());
			srp.ProcessMessage(serverMsg);
			
			//fecha a ligaçao ao servidor
		}
		
		clientNetwork.disconnetc(); 
		
		
	}
}


