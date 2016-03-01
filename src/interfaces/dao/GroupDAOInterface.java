package interfaces.dao;

import entities.Group;

public interface GroupDAOInterface {
	
	public Group getGroupByName(String groupName);

	public void addUserToGroup(String groupName, String username);

}
