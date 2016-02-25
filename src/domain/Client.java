package domain;
import network.ClientMessage;
import network.ClientNetwork;
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

		//validar input
		if(!argsParser.validateInput()) {
			System.exit(0);
		}
		
		clientNetwork = new ClientNetwork(argsParser.getServerIP(), argsParser.getServerPort());
		ClientMessage clientMessage = argsParser.getMessage();
		clientNetwork.connect();
		System.out.println("Cliente ligado ao servidor " + argsParser.getServerIP() + ":" + argsParser.getServerPort());
	
		/*
		clientNetwork.sendMessage(clientMessage);
		ServerResponseParser serverResponseParser = new ServerResponseParser(clientNetwork.receiveMessage());
		
		if (serverResponseParser.isValid()) {
			serverResponseParser.parseMessage();
		}
		*/
	}

}


