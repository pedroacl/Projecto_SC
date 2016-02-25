package network;

import java.util.ArrayList;

import entities.ChatMessage;

public class ServerMessage  {

	private ArrayList<ChatMessage> messages;
	
	private byte[] file;
	
	private MessageType messageType;

	
	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
}
