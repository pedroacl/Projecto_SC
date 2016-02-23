package factories;

import java.io.File;

import entities.User;

public class UserFactory {
	
	private static Long userId;

	public UserFactory() {
		userId = 0L;	
	}
	
	public User build(String username, String password) {
		User user = new User(username, password);
		user.setId(userId + 1);
		
		//criar pasta para o utilizador
		File file = new File("users/" + user.getId());
		
		if (!file.exists()) {
			if (!file.mkdir()) {
				System.out.println("Directorio criado");
			} else {
				System.out.println("Directorio nao criado");
			}	
		}
		
		//criar pasta para os ficheiros
		file = new File("users/" + user.getId() + "/");
		
		if (!file.exists()) {
			if (!file.mkdir()) {
				System.out.println("Directorio criado");
			} else {
				System.out.println("Directorio nao criado");
			}	
		}
		
		return user;
	}
}
