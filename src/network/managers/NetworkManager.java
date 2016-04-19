package network.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.crypto.SecretKey;

import network.messages.ChatMessage;
import network.messages.ClientNetworkMessage;
import network.messages.MessageType;
import network.messages.NetworkMessage;

public abstract class NetworkManager {
	
	protected static final int PACKAGE_SIZE = 1024;

	protected Socket socket;

	protected ObjectInputStream in;

	protected ObjectOutputStream out;

	public NetworkManager() {

	}

	/**
	 * 
	 * @param socket
	 */
	public NetworkManager(Socket socket) {
		this.socket = socket;

		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Função que pertmite fechar o socket do cliente atual
	 */
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Obtem uma determinado objecto enviado pela rede
	 * 
	 * @return O objecto enviado pela rede
	 */
	public Object receiveMessage() {
		Object networkMessage = null;

		try {
			networkMessage = in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return networkMessage;
	}

	public int getPackageSize() {
		return PACKAGE_SIZE;
	}

	/**
	 * Envia uma determinada mensagem pela rede
	 * 
	 * @param message
	 *            Mensagem a ser enviada pela rede
	 * @return
	 */
	protected boolean send(Object message) {
		boolean sent = true;
		try {
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
			sent = false;
		}

		return sent;
	}


	/**
	 * Envia uma mensagem pela rede
	 * 
	 * @param message
	 *            Mensagem a ser enviada
	 */
	public boolean sendMessage(NetworkMessage message) {
		return send(message);
	}

	
}
