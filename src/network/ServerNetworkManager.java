package network;

import java.net.Socket;

public class ServerNetworkManager extends NetworkManager {

	public ServerNetworkManager(Socket socket) {
		super(socket);
	}

	@Override
	public boolean sendFile(NetworkMessage message) {
		// TODO Auto-generated method stub
		return false;
	}
}
