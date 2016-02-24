package network;

import java.io.Serializable;

import entities.ChatMessage;

public class ClientMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 90127206322736048L;

	private String username;

	private String password;
	
	private MessageType messageType;
	
	public ClientMessage(String username, String password) {
		this.username = username;
		this.password = password;
	}
	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	@Override
	public String toString() {
		return "ClientMessage [username=" + username + ", password=" + password + ", messageType=" + messageType + "]";
	}
}
