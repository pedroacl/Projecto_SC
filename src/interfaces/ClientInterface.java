package interfaces;

import java.net.Socket;

import Domain.Request;

public interface ClientInterface {
	
	void enviarMensagem(Request request, Socket socket);
	
	
}
