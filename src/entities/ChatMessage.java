package entities;


import java.io.Serializable;
import java.util.Date;

import network.MessageType;



public class ChatMessage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8053113085617628635L;

	private Long id;
	
	private String content;
	
	private String fromUser;
	
	private String destination;
	
	private MessageType type;
	
	private Date createdAt;
	

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	public ChatMessage( String from, String destination, String message, MessageType type ) {
		this.fromUser = from;
		this.destination = destination;
		this.type = type;
		this.content = message;
		createdAt = new Date();
	}
	
	
	public Long getId() {
		return id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public String getFromUser() {
		return fromUser;
	}

	public String getDestination() {
		return destination;
	}

	public String getContent() {
		return content;
	}
	
	public MessageType getMessageType() {
		return type;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID: " + id + "\n");
		sb.append("From: " + fromUser + "\n");
		sb.append("To: " + destination + "\n");
		sb.append("Content: " + content + "\n");
		sb.append("Data: " + createdAt + "\n");
		
		return sb.toString();
	}
}
