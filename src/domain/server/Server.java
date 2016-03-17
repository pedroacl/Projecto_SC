package domain.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	 * @param args Argumentos passados pelo utilizador na linha de comandos
	 */
	public static void main(String[] args) {
		// nao existem parametros
		if (args.length == 0) {
			System.err.println("Por favor introduza o numero do porto.");
			System.exit(-1);
		}

		int serverPort = Integer.parseInt(args[0]);
		ServerSocket serverSocket = null;
		
		//cria serverSocket
		try {
			serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Thread Pool
		ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);

		System.out.println("Servidor inicializado e ah espera de pedidos.");

		while (true) {
			Socket socket = null;

			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Cliente ligado!");

			ServerThread serverThread = new ServerThread(socket);
			executorService.execute(serverThread);
		}

		// executorService.shutdown();
		// serverSocket.close();
	}
}
