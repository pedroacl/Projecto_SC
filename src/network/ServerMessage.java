package network;

import java.io.Serializable;
import java.util.ArrayList;

import entities.ChatMessage;

public class ServerMessage extends NetworkMessage implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<ChatMessage> messages;
	
	public ServerMessage() {
		super();
		messages = new ArrayList<ChatMessage>();
	}
	
	public ServerMessage(MessageType messageType) {
		super();	
	}

	public void setMessages(ArrayList<ChatMessage> list) {
		this.messages = list;
	}
	
	public ArrayList<ChatMessage> getMessageList() {
		return this.messages;
	}
	
}
