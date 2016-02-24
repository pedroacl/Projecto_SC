package interfaces;

import java.net.Socket;

import entities.ChatMessage;

public interface ClientInterface {
	
	void enviarMensagem(ChatMessage request, Socket socket);
	
	
}
