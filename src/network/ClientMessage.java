package network;

import java.io.Serializable;


public class ClientMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 90127206322736048L;

	private String username;
	
	private String destination;

	private String password;
	
	private MessageType messageType;
	
	private byte [] file;
	
	private String message;
	
	public ClientMessage(String username, String password, MessageType type) {
		this.username = username;
		this.password = password;
		messageType = type;
	}
	

	public String getPassword() {
		return password;
	}


	public String getUsername() {
		return username;
	}

	
	public byte[] getFile() {
		return file;
	}


	public void setFile(byte[] file) {
		this.file = file;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setDestination(String to) {
		this.destination = to;
	}
	
	public String getDestination() {
		return destination;
	}


	public MessageType getMessageType() {
		return messageType;
	}


	@Override
	public String toString() {
		return "ClientMessage [username=" + username + ", password=" + password + ", messageType=" + messageType + "]";
	}
}
