package entities;

import java.io.Serializable;
import java.util.Date;

public class Conversation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6123506731411848219L;

	private Long id;

	private String fromUser;

	private String toUser;

	private Date lastMessageDate;

	public Date getLastMessageDate() {
		return lastMessageDate;
	}

	public void setLastMessageDate(Date lastMessageDate) {
		this.lastMessageDate = lastMessageDate;
	}

	private Conversation() {

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

	@Override
	public boolean equals(Object obj) {
		Conversation conversation = (Conversation) obj;
		return (this.id == conversation.getId());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Id: " + id + "\n");
		sb.append("From: " + fromUser + "\n");
		sb.append("To: " + toUser + "\n");

		return sb.toString();
	}
}
