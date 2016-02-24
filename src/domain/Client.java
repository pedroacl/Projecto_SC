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
		
		ArgsParser inputParser = new ArgsParser(args);

		//validar input
		if(inputParser.validateInput()) {
			System.exit(0);
		}
		
		clientNetwork = new ClientNetwork();
		clientNetwork.connect(inputParser.getServerIP(), inputParser.getServerPort());
		clientNetwork.sendMessage(inputParser.getMessage());

		ServerResponseParser serverResponseParser = new ServerResponseParser(clientNetwork.getMessage());
		
		if (serverResponseParser.isValid()) {
			serverResponseParser.parseMessage();
		}
	}

}


