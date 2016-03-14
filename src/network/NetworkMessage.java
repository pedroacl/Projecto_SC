package network;

import java.io.Serializable;

public class NetworkMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int fileSize;
	
	protected MessageType messageType;
	
	private String content;
	
	public NetworkMessage() {
		
	}
	
	public NetworkMessage(MessageType messageType) {
		this.messageType = messageType;
	}
	
	public int getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
}
