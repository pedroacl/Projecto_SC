package domain;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import network.ServerNetwork;

/**
 * Classe que representa o servidor. Tem a lógica do negocio. Responde perante
 * os pedidos do cliente
 * 
 * @author Pedro, José, Antonio
 *
 */

public class Server {

	private static final int MAX_THREADS = 5;

	public static void main(String[] args) {

		int serverPort = Integer.parseInt(args[0]);
		ServerNetwork serverNetwork = new ServerNetwork(serverPort);

		// Thread Pool
		ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);

		System.out.println("Servidor inicializado e ah espera de pedidos.");

		while (true) {
			Socket socket = serverNetwork.getRequest();
			System.out.println("Cliente ligado!");

			ServerThread serverThread = new ServerThread(socket);
			executorService.execute(serverThread);
		}
		
        //executorService.shutdown();
	}
}
