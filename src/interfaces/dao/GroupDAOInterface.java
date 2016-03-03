package interfaces.dao;

import java.util.HashMap;

import entities.Group;

public interface GroupDAOInterface {
	
	public Long createGroup(String groupName, String admin);
	
	public Group getGroupByName(String groupName);
	
	public Long getGroupId(String groupName);

	public void addUserToGroup(Group group, String username);
	
	public HashMap<String, Group> getGroups();

}
