package interfaces.service;

public interface GroupServiceInterface {
	public boolean addUserToGroup(String username, String userToAdd, String groupName);

	public boolean removeUserFromGroup(String username, String userToAdd, String groupName);
}
