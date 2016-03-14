package interfaces.dao;

import java.util.concurrent.ConcurrentHashMap;

import entities.Group;

public interface GroupDAOInterface {
	
	public Long createGroup(String groupName, String admin);
	
	public Group getGroupByName(String groupName);
	
	public ConcurrentHashMap<String, String> getGroups();

	public boolean addUserToGroup(String username, String groupName);

}
