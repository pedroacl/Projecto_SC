package entities;

import java.io.Serializable;
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
		
	}
	
	public Conversation(String fromUser, String toUser) {
		this.fromUser = fromUser;
		this.toUser = toUser;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	
	
	public List<ChatMessage> getChatMessages() {
		return chatMessages;
	}

	public void setChatMessages(List<ChatMessage> chatMessages) {
		this.chatMessages = chatMessages;
	}

	public ChatMessage getFirstChatMessage() {
		return chatMessages.get(0);
	}
}
