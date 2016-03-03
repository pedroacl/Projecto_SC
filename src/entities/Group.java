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

	private Long id;
	
	private String name;
	
	private String admin;
	
	private Date createdAt;
	
	private List<String> users;
	
	
	public Group(String name, String admin) {
		this.name = name;
		this.admin = admin;
		this.createdAt = new Date();
		
		users = new ArrayList<String>();
		users.add(admin);
	}
	
	public void addUser(String username) {
		if (!users.contains(username))
			users.add(username);
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

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
}
