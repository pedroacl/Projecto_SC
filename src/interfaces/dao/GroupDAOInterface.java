package interfaces.dao;

import java.util.concurrent.ConcurrentHashMap;

import entities.Group;

public interface GroupDAOInterface {
	
	public Long createGroup(String groupName, String admin);
	
	public ConcurrentHashMap<String, String> getGroups();

	public boolean addUserToGroup(String username, String groupName);

}
