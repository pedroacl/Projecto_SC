package entities;


import java.io.Serializable;
import java.util.Date;

import network.MessageType;



public class ChatMessage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8053113085617628635L;

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
	
	@Override
	public boolean equals(Object obj) {
		ChatMessage chatMessage = (ChatMessage) obj;

		return chatMessage.content.equals(this.content) &&
				chatMessage.fromUser.equals(this.fromUser) &&
				chatMessage.destination.equals(this.destination) &&
				chatMessage.type.equals(this.type) &&
				chatMessage.createdAt.equals(this.createdAt);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("From: " + fromUser + "\n");
		sb.append("To: " + destination + "\n");
		sb.append("Content: " + content + "\n");
		sb.append("Data: " + createdAt + "\n");
		
		return sb.toString();
	}
}
