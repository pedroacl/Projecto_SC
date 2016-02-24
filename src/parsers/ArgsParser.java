package parsers;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import entities.ChatMessage;

public class ArgsParser {
	
	private String username;
	
	private String password;
	
	private String serverIP;

	private String serverPort;
	
	private boolean isValid;
	
	private String action;
	
	
	public String getServerIP() {
		return serverIP;
	}


	public String getServerPort() {
		return serverPort;
	}

	
	public ArgsParser() {
		isValid = false;
	}
	
	
	public ArgsParser(String[] args) {
		this();

		if(args.length < 4 || args.length > 8) {
			System.out.println("xau 0");
		}
		
		//verifica 1º parametro(nome da aplicação)
		if(! args[0].equals("myWhats")) {
			System.out.println("xau 1");
		}
		
		//coloca nome de utilizador na segunda posiçao do novo array
		username = args[1];
		
		//verifica 3º parametro (serverAddress)
		if(!args[2].matches("(\\d+\\.){3}(\\d:\\d+)")){
			System.out.println(args[2] +"xau 2");
		}
		
		//obter IP e porto do servidor
		String[] address = args[2].split(":");
		serverIP = address[0];
		serverPort = address[1];
		
		//coloca serverAddress na primeira posição do array
		
		//Verifica se o utilizador colocou password e que os parametros estão corretos
		if(args[3].equals("-p") && args.length > 4) {
			password = args[4];
			action = parseAction(args, 5); //pode retornar caso parametros incorrectos
		}
		else {
			password = null;
			action = parseAction(args,3); //pode retornar caso parametros incorrectos
		}
		
		// se nao houver parametros errados retorna um novo array de argumentos estruturado,
		if(action == null)
			System.out.println("xau3");
		
	}
	
	
	public boolean validateInput() {
		return isValid;
	}
	

	public ChatMessage getMessage() {
		//TODO
		return new ChatMessage();
	} 	

	
	private static String parseAction(String[] args, int i) {
		
		if(args.length == i)
			return null;
		
		System.out.println("tamanho: " + args.length + " i: " + i);
		
		System.out.println("print: " + args[i]);
		
		String res;
		
		switch(args[i]) {
			case"-m":
				if(args.length == i+3) 
					res = args[i] + " " +args[i+1] + " " + args[i+2];
				else {
					System.out.println("adeus 1");
					res = null;
				}
				break;
				
			case "-f":
				if(args.length == i+3) 
					res = args[i] + " " +args[i+1] + " " + args[i+2];
				else {
					System.out.println("adeus 2");
					res = null;
				}
				break;
				
			case "-r" :
				if(args.length == i + 1 ) 
					res = args[i];
				else
					if(args.length == i + 2)
					res = args[i] + " " + args[i+1];
					else
						if(args.length == i + 3)
							res = args[i] + " " +args[i+1] + " " + args[i+2];
						else {
							System.out.println("adeus 3");
							res = null;
						}
				break;
			case "-a":
				if(args.length == i+3) 
					res = args[i] + " " +args[i+1] + " " + args[i+2];
				else {
					System.out.println("adeus 4");
					res = null;
				}
				break;
			case "-d":
				if(args.length == i+3) 
					res = args[i] + " " +args[i+1] + " " + args[i+2];
				else {
					System.out.println("adeus 5");
					res = null;
				}
				break;
			default: {
				System.out.println("adeus 6");
				res = null;
			}
				
		}
		
		return res;
		
	}

	
	public static void enviarMensagem(ChatMessage request, Socket socket) throws IOException {
		
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(request);
		oos.close();
		
	}
	
	
	/*
	 * imprime a mensagem  de como usar a aplicação
	 */
	public static void printUsage() {
		System.out.println("exemplo de uso:");
		System.out.println("myWhats <localUser> <serverAddress> [ ‐p <password> ] "
				+ "[ ‐m <contact> <message> | ‐f <contact> <file>  | ‐r contact file  |  "
				+ "‐a <user> <group> |  ‐d <user> <group>  ]");
	}
	
}