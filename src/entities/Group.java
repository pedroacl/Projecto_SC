package entities;
import java.util.List;
import java.util.Date;

public class Group {
	
	public Group() {
		
	}
	
	private User owner;
	
	private Date createdAt;
	
	private List<User> users;
	
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
