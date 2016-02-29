package entities;

import java.io.Serializable;

public class ConversationHeader implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4127367441628803115L;

	private Long conversationId;
	
	private String toUser;
	
	
	public ConversationHeader(Long conversationId, String toUser) {
		this.conversationId = conversationId;
		this.toUser = toUser;
	}
	

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public Long getConversationId() {
		return conversationId;
	}
}
