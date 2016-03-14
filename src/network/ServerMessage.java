package network;

import java.util.ArrayList;
import java.util.List;

import entities.ChatMessage;

public class ServerMessage extends NetworkMessage {

	private static final long serialVersionUID = 1L;
	
	private List<ChatMessage> messages;

	public ServerMessage() {
		super();
		messages = new ArrayList<ChatMessage>();
	}

	public ServerMessage(MessageType messageType) {
		super(messageType);
	}

	public void setMessages(ArrayList<ChatMessage> list) {
		this.messages = list;
	}

	public List<ChatMessage> getMessageList() {
		return this.messages;
	}

}
