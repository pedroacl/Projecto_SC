package interfaces.dao;

import java.util.concurrent.ConcurrentHashMap;

import entities.Group;

public interface GroupDAOInterface {
	
	public Long createGroup(String groupName, String admin);

	public void deleteGroup(String groupName);
	
	public ConcurrentHashMap<String, String> getGroups();

	public boolean addUserToGroup(String username, String groupName);

	public Group getGroup(String groupName);

	public void removeUserFromGroup(Group group, String userToRemove);

}
