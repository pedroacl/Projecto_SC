package entities;


import java.io.Serializable;
import java.util.Date;

import network.MessageType;



public class ChatMessage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8053113085617628635L;


	public ChatMessage() {
		createdAt = new Date();
	}

	public ChatMessage( String from, MessageType type ) {
		this();
		this.fromUser = from;
	}
	
	
	private String message;

	private byte [] file;
	
	private String fromUser;
	
	private String toUser;
	
	private Date createdAt;


	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setFileByte(byte [] file) {
		this.file = file;
	}

}
