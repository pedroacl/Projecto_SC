package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6123506731411848219L;
	
	private Long id;
	
	private String fromUser;
	
	private String toUser;
	
	private List<ChatMessage> chatMessages;


	public Conversation() {
		chatMessages = new ArrayList<ChatMessage>();
	}
	
	public Conversation(String fromUser, String toUser) {
		this();
		this.fromUser = fromUser;
		this.toUser = toUser;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	

		public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	
	public List<ChatMessage> getChatMessages() {
		return chatMessages;
	}


	public void addChatMessage(ChatMessage chatMessage) {
		System.out.println(chatMessage);
		chatMessages.add(chatMessage);
	}


	@Override
	public boolean equals(Object obj) {
		Conversation conversation = (Conversation) obj;
		return (this.id == conversation.getId());
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (ChatMessage chatMessage : chatMessages) {
			sb.append(chatMessage.toString());
			sb.append("\n");
		}

		return sb.toString();
	}
}
