package entities;
import java.util.List;
import java.util.Date;

public class Group {
	
	public Group() {
		
	}

	private Long id;
	
	private String name;
	
	private User owner;
	
	private Date createdAt;
	
	private List<String> users;
	
	
	public void addUser(String username) {
		users.add(username);
	} 
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
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

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public Long getId() {
		return id;
	}
}
