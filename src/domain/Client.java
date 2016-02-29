package domain;
import network.ClientMessage;
import network.ClientNetwork;
import network.ServerMessage;
import parsers.ArgsParser;


public class Client{
	
	private static ClientNetwork clientNetwork;
	
	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		System.out.println("argsLength = " + args.length );

		for(String s : args) {
			System.out.println(s);
		}
		
		ArgsParser argsParser = new ArgsParser(args);
		UserInterface userInterface = new UserInterface();
		
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
		clientNetwork.sendMessage(clientMessage);

		//recebe a resposta
		ServerMessage serverMsg = clientNetwork.receiveMessage();
		
		/*
		Cria e passa a resposta para o Parser que a vai processar
		ServerResponseParser serverResponseParser = new ServerResponseParser(userInterface,clientNetwork);
		serverResponseParser.processResponse(serverMsg);
		
		Fecha a ligação
		ClientNetWork.disconect();
		*/
	}
}


