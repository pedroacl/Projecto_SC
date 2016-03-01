package interfaces.dao;

import java.util.HashMap;

public interface UserDAOInterface {
	public HashMap<String, String> getUsers();
	
	public void addUser(String username, String password);
	
	public void deleteUser(String username);

}
