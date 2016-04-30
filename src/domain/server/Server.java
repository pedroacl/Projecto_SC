package domain.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import exceptions.InvalidMacException;
import util.SecurityUtils;
import util.UserUtil;

/**
 * Classe que representa o servidor. Tem a lógica do negocio. Responde perante
 * os pedidos do cliente
 * 
 * @author Pedro, José e António
 *
 */
public class Server {

	private static final int MAX_THREADS = 5;

	/**
	 * Função principal do servidor
	 * 
	 * @param args
	 *            Argumentos passados pelo utilizador na linha de comandos
	 */
	public static void main(String[] args) {
		// nao existem parametros
		if (args.length != 2) {
			System.err.println("Por favor introduza o numero do porto e a password.");
			System.exit(-1);
		}

		ServerSocketFactory ssf = null;
		ServerSocket serverSocket = null;

		System.setProperty("javax.net.ssl.keyStore", "keystore.servidor");
		System.setProperty("javax.net.ssl.keyStorePassword", "seguranca");

		// cria serverSocket
		try {
			int serverPort = Integer.parseInt(args[0]);
			ssf = SSLServerSocketFactory.getDefault();
			serverSocket = ssf.createServerSocket(serverPort);

		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}

		
		String password = args[1];
		

		// Thread Pool
		ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
		Authentication authentication = new Authentication(password);

		System.out.println("Validar ficheiros MAC");
		File users = new File("users.txt");
		
		if(users.exists()) {
			try {
				SecurityUtils.validateMacFiles(authentication.getServerPassword());
			} catch (InvalidMacException e) {
				executorService.shutdown();
				System.out.println("\nServidor terminado!");
				System.exit(0);
			}
			
			System.out.println("\nFicheiros MAC válidos.");
		}
		
		System.out.println("\nServidor inicializado e à espera de pedidos.");

		while (true) {
			Socket socket = null;

			try {
				socket = serverSocket.accept();

				System.out.println("Cliente ligado!");
				
				if(users.exists()){
					System.out.println("Validar ficheiros MAC");
					
					// validar ficheiros MAC para cada pedido
					SecurityUtils.validateMacFiles(authentication.getServerPassword());
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (InvalidMacException e) {
				e.printStackTrace();
				break;
			}

			ServerThread serverThread = new ServerThread(socket, authentication);
			executorService.execute(serverThread);
		}

		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		executorService.shutdown();

		System.out.println("\nServidor terminado!");
		System.exit(0);
	}
}
