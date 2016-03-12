package domain;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import network.ServerNetwork;
import network.ServerSocketNetwork;

/**
 * Classe que representa o servidor. Tem a lógica do negocio. Responde perante
 * os pedidos do cliente
 * 
 * @author Pedro, José, Antonio
 *
 */

public class Server {

	private static ServerNetwork serverNetwork;
	
	private static final int MAX_THREADS = 5;

	// TODO receber IP por argumentos
	public static void main(String[] args) {
		String portString = args[0];
		int serverPort = Integer.parseInt(portString);

		serverNetwork = new ServerNetwork(serverPort);

		// Thread Pool
		//ThreadFactory threadFactory = Executors.defaultThreadFactory();
		ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);

		System.out.println("Servidor inicializado e ah espera de pedidos.");

		while (true) {
			Socket socket = serverNetwork.getRequest();
			System.out.println("Cliente ligado!");

			ServerSocketNetwork serverSocketNetwork = new ServerSocketNetwork(socket);

			ServerThread serverThread = new ServerThread(serverSocketNetwork);
			//executorService.execute(serverThread);
			executorService.submit(serverThread);
			//serverThread.run();

			serverSocketNetwork.close();
		}
		
        //executorService.shutdown();

	}
}
