package factories;

import entities.User;

public class UserFactory {
	
	private static Long userId;

	public UserFactory() {
		userId = 0L;
	}
	
	public User build(String username, String password) {
		User user = new User(username, password);
		user.setId(userId + 1);
		
		return user;
	}
}
