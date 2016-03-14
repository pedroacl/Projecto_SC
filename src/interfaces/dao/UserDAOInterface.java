package interfaces.dao;

import java.util.concurrent.ConcurrentHashMap;

public interface UserDAOInterface {
	public ConcurrentHashMap<String, String> getUsers();
	
	public void addUser(String username, String password);
	
	public void deleteUser(String username);

}
