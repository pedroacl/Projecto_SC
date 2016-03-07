package interfaces;

public interface AuthenticationInterface {

	public boolean authenticateUser(String username, String password);

	public boolean existsUser(String username);

	public boolean existsGroup(String groupName);

	public void addUser(String username, String password);
	
	public void addGroup(String groupName, String ownerName);

}
