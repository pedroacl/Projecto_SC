package network;

public class ClientMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;

	private String destination;

	private String password;

	public ClientMessage(String username, String password, MessageType type) {
		super(type);
		this.username = username;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public void setDestination(String to) {
		this.destination = to;
	}

	public String getDestination() {
		return destination;
	}

	@Override
	public String toString() {
		return "ClientMessage [username=" + username + ", password=" + password + ", messageType=" + messageType
				+ "]";
	}
}
