package entities;


import java.io.Serializable;
import java.util.Date;

import network.MessageType;



public class ChatMessage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8053113085617628635L;

	
	private String message;
	
	private String fromUser;
	
	private String destination;
	
	private MessageType type;
	
	private Date createdAt;

	public ChatMessage( String from, String destination, MessageType type ) {
		this.fromUser = from;
		this.destination = destination;
		this.type = type;
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

	
	public String getMessage() {
		return message;
	}

	

}
