package interfaces;

import entities.Group;

public interface AuthenticationInterface {

	public boolean authenticateUser(String username, String password);

	public boolean exists(String name);
	
	public boolean existsUser(String username);

	public boolean existsGroup(String group);

	public void addUser(String username, String password);
	
	public void addGroup(String groupName, String ownerName);
	
	public String getGroupOwner(String groupName);

	public boolean addUserToGroup(String destination, Group group);
}
