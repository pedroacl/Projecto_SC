package network;

import java.util.ArrayList;

import entities.ChatMessage;

public class ServerMessage {

	private ArrayList<ChatMessage> messages;
	
	private int sizeFile;
	
	private MessageType messageType;
	
	private String content;
	
	public ServerMessage(MessageType type) {
		messageType = type;
	}

	
	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
}
