package entities;

import java.io.Serializable;

public class Conversation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6123506731411848219L;
	
	private Long id;
	
	private String fromUser;
	
	private String toUser;

	
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


	@Override
	public boolean equals(Object obj) {
		Conversation conversation = (Conversation) obj;
		
		return (this.id == conversation.getId());
	}
}
