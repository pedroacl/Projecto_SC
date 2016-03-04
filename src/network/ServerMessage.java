package network;

import java.io.Serializable;
import java.util.ArrayList;

import entities.ChatMessage;

public class ServerMessage implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<ChatMessage> messages;
	
	private int sizeFile;
	
	private MessageType messageType;
	
	private String content;
	

	public ServerMessage(MessageType type) {
		messageType = type;
	}
	
	
	public String getContent() {
		return content;
	}
	
	public void setSizeFile(int sizeFile) {
		this.sizeFile = sizeFile;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	
	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public String getMessage() {
		
		return content;
	}

	public int getFileSize() {
		return sizeFile;
	}
	
	public void setMessages(ArrayList<ChatMessage> list) {
		this.messages = list;
	}
	
}
