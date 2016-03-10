package interfaces.service;

public interface GroupServiceInterface {

	public Long createGroup(String groupName, String admin);

	public boolean addUserToGroup(String username, String userToAdd, String groupName);

	public boolean removeUserFromGroup(String username, String userToAdd, String groupName);

	public String getGroupOwner(String groupName);

	boolean existsGroup(String groupName);
}
