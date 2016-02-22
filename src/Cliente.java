import java.util.Scanner;


public class Cliente {

	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		System.out.println("argsLength = " + args.length );
		for(String s : args) {
			System.out.println(s);
		}
		
		String [] parameters = parseArgs(args);
		if(parameters == null) {
			printUsage();
			System.exit(0);
		}
		
		if(parameters[2] == null) {
			Scanner sc = new Scanner(System.in);
			System.out.println("qual é a password?");
			parameters[2] = sc.next();
			sc.close();
		}
		
		System.out.println("Adress= " + parameters[0]);
		System.out.println("User = " + parameters[1]);
		System.out.println("Password= " + parameters[2]);
		System.out.println("Acção= " + parameters[3]);
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
	
	/**
	 * Analisa os argumentos passados pelo utilizador
	 *@ param: args - Array de String com os parametros passados pelo utilizador
	 *@ return: Array de String com tamnho 4 :[ ADRESS | USER | PASSOWRD ou NULL | ACTION]
	 *			ou NULL caso os parametros estejam errados
	 */
	private static String [] parseArgs (String [] args) {
		
		if(args.length < 4 || args.length > 8) {
			System.out.println("xau 0");
			return null;
		}
		
		String [] newArgs = new String[4];
		
		//verifica 1º parametro(nome da aplicação)
		if(! args[0].equals("myWhats")) {
			System.out.println("xau 1");
			return null;
		}
		
		//coloca nome de utilizador na segunda posiçao do novo array
		newArgs[1] = args[1];
		
		//verifica 3º parametro (serverAddress)
		if(!args[2].matches("(\\d+\\.){3}(\\d:\\d+)")){
			System.out.println(args[2] +"xau 2");
			return null;
		}
		
		//coloca serverAddress na primeira posição do array
		newArgs[0] = args[2];
		
		//Verifica se o utilizador colocou password e que os parametros estão corretos
		if(args[3].equals("-p") && args.length > 4) {
			newArgs[2] = args[4];
			newArgs[3] = parseAction(args,5); //pode retornar caso parametros incorrectos
		}
		else {
			newArgs[2] = null;
			newArgs[3] = parseAction(args,3); //pode retornar caso parametros incorrectos
		}
		
		// se nao houver parametros errados retorna um novo array de argumentos estruturado,
		if(newArgs[3] == null)
			System.out.println("xau3");
		return newArgs[3] == null ? null : newArgs;
	
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
	

}


