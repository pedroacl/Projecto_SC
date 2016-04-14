package network.messages;

import java.util.ArrayList;

import entities.ChatMessage;

public class ServerMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<ChatMessage> messages;

	public ServerMessage(MessageType messageType) {
		super(messageType);
		messages = new ArrayList<ChatMessage>();
	}

	public void setMessages(ArrayList<ChatMessage> list) {
		this.messages = list;
	}

	public ArrayList<ChatMessage> getMessageList() {
		return this.messages;
	}

}
