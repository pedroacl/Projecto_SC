package entities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Group implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3719430979913465539L;

	private Long conversationId;
	
	private String name;
	
	private String admin;
	
	private Date createdAt;
	
	private List<String> users;
	
	
	public Group(String name, String admin, Long conversationId) {
		this.name = name;
		this.admin = admin;
		this.createdAt = new Date();
		this.conversationId = conversationId;
		
		users = new ArrayList<String>();
		users.add(admin);
	}
	
	public boolean addUser(String username) {
		if (!users.contains(username)) {
			users.add(username);
			return true;
		}
		
		return false;
	} 
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getAdmin() {
		return admin;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public List<String> getUsers() {
		return users;
	}

	public Long getConversationId() {
		return conversationId;
	}
}
