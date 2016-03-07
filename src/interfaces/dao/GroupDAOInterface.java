package interfaces.dao;

import java.util.HashMap;

import entities.Group;

public interface GroupDAOInterface {
	
	public Long createGroup(String groupName, String admin);
	
	public Group getGroupByName(String groupName);
	
	public HashMap<String, String> getGroups();

	public void addUserToGroup(Group group, String username);
	
}
