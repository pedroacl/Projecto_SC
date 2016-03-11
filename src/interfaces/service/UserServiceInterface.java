package interfaces.service;

import java.util.HashMap;

public interface UserServiceInterface {

	public HashMap<String, String> getUsers();

	public void addUser(String username, String password);

	public String getUserPassword(String username);
}
