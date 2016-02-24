package domain;
import network.ClientNetwork;
import parsers.ArgsParser;
import parsers.ServerResponseParser;


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
		if(argsParser.validateInput()) {
			System.exit(0);
		}
		
		clientNetwork = new ClientNetwork();
		clientNetwork.connect(argsParser.getServerIP(), argsParser.getServerPort());
		clientNetwork.sendMessage(argsParser.getMessage());

		ServerResponseParser serverResponseParser = new ServerResponseParser(clientNetwork.getServerMessage());
		
		if (serverResponseParser.isValid()) {
			serverResponseParser.parseMessage();
		}
	}

}


