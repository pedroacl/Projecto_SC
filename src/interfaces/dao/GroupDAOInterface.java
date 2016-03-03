package interfaces.dao;

import java.util.HashMap;

import entities.Group;

public interface GroupDAOInterface {
	
	public Group getGroupByName(String groupName);
	
	public Long getGroupId(String groupName);

	public void addUserToGroup(String groupName, String username);
	
	public HashMap<String, Group> getGroups();

}
