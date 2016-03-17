package domain.client;

import java.io.File;

import network.messages.ClientMessage;
import network.messages.MessageType;

/**
 * Classe que verifica os parametros de entrada e cria um ClientMessage
 * correspondente a opção do utilizador
 * 
 * @author Pedro, Jose, Antonio
 *
 */
public class ArgsParser {

	private String username;

	private String[] args;

	private String password;

	private String serverIP;

	private String serverPort;

	private boolean isValid;

	private String action;

	private String[] act;

	/**
	 * Devolve o ip do servidor
	 * 
	 * @return String com a representaçao do ip do servidor a contactar
	 * @requires isValid == true
	 */
	public String getServerIP() {
		return serverIP;
	}

	/**
	 * Devolve o porto do servidor selecionado pelo utilizador
	 * 
	 * @return String com o porto
	 * @requires isValid == True
	 */
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

	/**
	 * Verifica se os argumentros de entrada são válidos.
	 * 
	 * @return true, caso seja uma opção válida, false caso contrario
	 */
	public boolean validateInput() {
		if (args.length < 4 || args.length > 8)
			return false;

		// verifica 1º parametro(nome da aplicação)
		if (!args[0].equals("myWhats"))
			return false;

		// coloca nome de utilizador na segunda posiçao do novo array
		username = args[1];

		// verifica 3º parametro (serverAddress)
		if (!args[2].matches("(\\d+\\.){3}(\\d:\\d+)"))
			return false;

		// obter IP e porto do servidor
		String[] address = args[2].split(":");
		serverIP = address[0];
		serverPort = address[1];

		// Verifica se o utilizador colocou password e que os parametros estão
		// corretos
		if (args[3].equals("-p") && args.length > 4) {
			password = args[4];
			action = parseAction(args, 5); // pode retornar caso parametros
											// incorrectos
		} else {
			password = null;
			action = parseAction(args, 3); // pode retornar caso parametros
											// incorrectos
		}

		// se nao houver parametros errados retorna um novo array de argumentos
		// estruturado,
		if (action == null)
			return false;

		act = action.split(" ");

		// verifica se o username e o destinatario são diferentes
		if (!act[0].equals("-r") && !act[0].equals("-d")
				&& act[1].equals(username)) {
			System.out.println("Destinatário igual ao username");
			return false;
		}

		isValid = true;
		return true;
	}

	/**
	 * Informa sobre a validade dos argumentos de entrada
	 * 
	 * @return True, caso a analise dos argumentos de entrada ter dado positivo
	 */
	public boolean isValidInput() {
		return isValid;
	}

	/**
	 * Devolve um clientMessage preenchida conforme as opçoes do utilizador
	 * 
	 * @return ClientMessage, mensagem com pedido do cliente ao servidor
	 * @requires isValid == True
	 */
	public ClientMessage getMessage() {
		ClientMessage pedido = null;

		switch (act[0]) {
		case "-m":
			pedido = new ClientMessage(username, password, MessageType.MESSAGE);
			pedido.setDestination(act[1]);
			StringBuilder sb = new StringBuilder();

			for (int i = 2; i < act.length; i++)
				sb.append(act[i] + " ");

			pedido.setContent(sb.toString());
			break;

		case "-f":
			pedido = new ClientMessage(username, password, MessageType.FILE);
			pedido.setDestination(act[1]);
			pedido.setFileSize(fileSize(act[2]));
			pedido.setContent(act[2]); // coloca nome do ficheiro na mensagem
			break;

		case "-a":
			pedido = new ClientMessage(username, password, MessageType.ADDUSER);
			pedido.setDestination(act[1]); // coloca user a adicionar no destino
			pedido.setContent(act[2]); // coloca nome do grupo na mensagem
			break;

		case "-d":
			pedido = new ClientMessage(username, password,
					MessageType.REMOVEUSER);

			pedido.setDestination(act[1]); // coloca user a adicionar no destino
			pedido.setContent(act[2]); // coloca nome do grupo na mensagem
			break;

		case "-r":
			if (act.length == 1) {
				pedido = new ClientMessage(username, password,
						MessageType.RECEIVER);

				pedido.setContent("recent");
			} else if (act.length == 2) {
				pedido = new ClientMessage(username, password,
						MessageType.RECEIVER);
				pedido.setDestination(act[1]);
				pedido.setContent("all");
			} else if (act.length == 3) {
				pedido = new ClientMessage(username, password,
						MessageType.RECEIVER);

				pedido.setDestination(act[1]);// coloca no destinatario a quem
												// pede o file
				pedido.setContent(act[2]); // coloca nome do ficheiro na
											// mensagem
			}

			break;
		}

		return pedido;

	}

	// Devolve a ação propriamente dita a fazer
	private static String parseAction(String[] args, int i) {
		if (args.length == i)
			return null;

		String res;

		switch (args[i]) {
		case "-m":
			if (args.length == i + 3)
				res = args[i] + " " + args[i + 1] + " " + args[i + 2];
			else {
				res = null;
			}
			break;

		case "-f":
			if (args.length == i + 3) {
				res = args[i] + " " + args[i + 1] + " " + args[i + 2];
				File file = new File(args[i + 2]);

				if (!file.isFile() || file.length() >= Integer.MAX_VALUE) {
					System.out.println("Ficheiro não existe ou excede o limite");
					res = null;
				}
			} else {
				res = null;
			}

			break;

		case "-r":
			if (args.length == i + 1)
				res = args[i];
			else if (args.length == i + 2)
				res = args[i] + " " + args[i + 1];
			else if (args.length == i + 3)
				res = args[i] + " " + args[i + 1] + " " + args[i + 2];
			else {
				res = null;
			}

			break;

		case "-a":
			if (args.length == i + 3)
				res = args[i] + " " + args[i + 1] + " " + args[i + 2];
			else {
				res = null;
			}

			break;

		case "-d":
			if (args.length == i + 3)
				res = args[i] + " " + args[i + 1] + " " + args[i + 2];
			else {
				res = null;
			}
			break;
		default: {
			res = null;
		}
		}

		return res;

	}

	/**
	 * Devolve o utilizador que está a usar o sistema
	 * 
	 * @return nome do utilizador a usar o sistema
	 * @requires isValid == True
	 */
	public String getUsername() {
		return username;
	}

	// devolve o tamanho do ficheiro que o utilizador quer enviar
	private int fileSize(String name) {
		File file = new File(name);
		return (int) file.length();
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Verifica se o utilizador preencheu o campo password
	 * 
	 * @return True,caso a password tenha sido dado pelo utilizador
	 * @requires isValid = true;
	 */
	public boolean passwordFilled() {
		return password != null;
	}
}
