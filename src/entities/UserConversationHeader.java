package entities;

import java.io.Serializable;

public class UserConversationHeader implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4127367441628803115L;

	private Long conversationId;
	
	private String toUser;
	
	
	public UserConversationHeader(String toUser, Long conversationId) {
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
