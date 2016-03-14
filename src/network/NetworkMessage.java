package network;

public class NetworkMessage {
	private int fileSize;
	
	private MessageType messageType;
	
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
