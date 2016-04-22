package network.messages;

public class ClientNetworkMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;

	private String password;

	private String destination;


	public ClientNetworkMessage(String username, String password, MessageType type) {
		super(type);
		this.username = username;
		this.password = password;
	}

	/**
	 * Getters e Setters 
	 */
	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public String toString() {
		return "ClientMessage [username=" + username + ", password=" + password + ", messageType=" + messageType + "]";
	}
}
