package entities;

import java.io.Serializable;

public class Conversation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6123506731411848219L;
	
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Conversation() {
		
	}
	
	public Conversation(String fromUser, String toUser) {
		
	}
}
