package entities;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 523914928523601517L;
	
	private Long id;
	
	private String fromUser;
	
	private String toUser;
	
	private Date createdAt;

	public Message() {
		id = 0L;
		createdAt = new Date();
	}

	public Message(String fromUser, String toUser) {
		Message message = new Message();
		
		message.fromUser = fromUser;
		message.toUser = toUser;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
