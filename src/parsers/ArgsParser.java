package parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import network.ClientMessage;
import network.MessageType;

public class ArgsParser {
	
	private String username;

	private String [] args;
	
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
		this.args = args;		
	}
	
	
	public boolean validateInput() {
		if(args.length < 4 || args.length > 8) {
			System.out.println("xau 0");
			return false;
		}
		
		//verifica 1º parametro(nome da aplicação)
		if(! args[0].equals("myWhats")) {
			System.out.println("xau 1");
			return false;
		}
		
		//coloca nome de utilizador na segunda posiçao do novo array
		username = args[1];
		
		//verifica 3º parametro (serverAddress)
		if(!args[2].matches("(\\d+\\.){3}(\\d:\\d+)")){
			System.out.println(args[2] +"xau 2");
			return false;
		}
		
		//obter IP e porto do servidor
		String[] address = args[2].split(":");
		serverIP = address[0];
		serverPort = address[1];
		

		
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
		if(action == null) {
			System.out.println("xau3");
			return false;
		}
		
		isValid = true;
		return true;
	}
	
	public boolean isValidInput() {
		return isValid;
	}
	

	public ClientMessage getMessage() {
		String [] act = action.split(" ");
		ClientMessage pedido = null;
		
		switch(act[0]) {
		case "-m":
			pedido = new ClientMessage(username, password, MessageType.MESSAGE);
			pedido.setDestination(act[1]);
			StringBuilder sb = new StringBuilder();
			for(int i = 2; i < act.length; i++) {
				sb.append(act[i] + " ");
			}
			pedido.setMessage(sb.toString());
			break;
		case "-f":
			pedido = new ClientMessage(username, password, MessageType.FILE);
			pedido.setDestination(act[1]);
			System.out.println("Client: fileSize= "+ fileSize(act[2]));
			pedido.setFileSize(fileSize(act[2]));
			
			/*
			try {
				pedido.setFile(transformFiletoByte(act[2]));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			pedido.setMessage(act[2]);
			break;
		case "-a":
			pedido = new ClientMessage(username, password, MessageType.ADDUSER);
			pedido.setDestination(act[2]);
			break;
		
		case "-d":
			pedido = new ClientMessage(username,password, MessageType.REMOVEUSER);
			pedido.setDestination(act[2]);
			break;
			
		case "-r":
			if(act.length == 1) {
				pedido = new ClientMessage(username, password, MessageType.RECEIVER);
				pedido.setMessage("recent");
			}
			if(act.length == 2) {
				pedido = new ClientMessage(username, password, MessageType.RECEIVER);
				pedido.setDestination(act[1]);
				pedido.setMessage("all");
			}
			if(act.length == 3) {
				pedido = new ClientMessage(username, password, MessageType.RECEIVER);
				pedido.setDestination(act[1]);
				pedido.setMessage(act[2]);
			}
			break;
		}
		
		return pedido;
				
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
					res = null;
				}
				break;
				
			case "-f":
				if(args.length == i+3) {
					res = args[i] + " " +args[i+1] + " " + args[i+2];
					File file = new File(args[i+2]);
					if(!file.isFile() || file.length() >= Integer.MAX_VALUE) {
						
						System.out.println(file.getPath());
						System.out.println("ficheiro não existe ou excede limite");
						res = null;
					}
				}
					
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
	
	public String getUsername() {
		return username;
	}
	
	//PAra REMOVER
	private byte[] transformFiletoByte(String path) throws IOException {
		
		File file = new File(path);
		byte [] bFile = new byte[(int) file.length()]; //temos de limitar o tamanho do ficheiro
		
		FileInputStream fileInputStream = new FileInputStream(file);
	    int length = fileInputStream.read(bFile);
	    if(length != file.length())
	    	System.out.print("ERRO na transformação do ficheiro para byte");
	    	
	    fileInputStream.close();
	    
		return bFile;
	}
	
	private int fileSize(String name) {
		File file = new File(name);
		return (int) file.length();
	}

}
