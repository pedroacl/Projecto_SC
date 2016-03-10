package interfaces;

public interface AuthenticationInterface {

	public boolean authenticateUser(String username, String password);

	public boolean existsUser(String username);

	public void addUser(String username, String password);

}
