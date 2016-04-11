package interfaces.service;

import java.util.concurrent.ConcurrentHashMap;

public interface UserServiceInterface {

	public ConcurrentHashMap<String, String> getUsers();

	public void addUser(String username, String password);

	public String[] getUserPasswordAndSalt(String username);
}
