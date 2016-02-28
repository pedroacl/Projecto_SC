package entities;

public class ConversationHeader {
	private Long Id;
	
	private String toUser;
	
	
	public ConversationHeader(Long conversationId, String toUser) {
		this.Id = conversationId;
		this.toUser = toUser;
	}
	

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public Long getId() {
		return Id;
	}
}
