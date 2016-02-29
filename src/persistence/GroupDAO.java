package persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import entities.Group;

public class GroupDAO {
	
	private static GroupDAO groupDAO = new GroupDAO();
	
	
	private GroupDAO() {
		
	}
	
	
	public GroupDAO getInstance() {
		return groupDAO;
	}
	
	
	/**
	 * 
	 * @param groupName
	 * @return
	 */
	public Group getGroupByName(String groupName) {
		File file = new File("groups/" + groupName + "/group");
		Group group = null;

		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			group = (Group) objectInputStream.readObject();

			objectInputStream.close();
			fileInputStream.close();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		return group;
	}
	
	
	/**
	 * Adicionar utilizador a um grupo
	 * @param groupName
	 * @param username
	 */
	public void addUserToGroup(String groupName, String username) {
		File file = new File("groups/" + groupName + "/group");
	
		//ficheiro nao existe
		if (!file.exists()) {
			file.getParentFile().mkdirs();

			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Group group = null;
		
		//atualizar ficheiro
		try {
			group = getGroupByName(groupName);
		
			//utilizador já adicionado ao grupo
			if (group.getUsers().contains(username)) {
				return;
			}
			
			//adicionar utilizador ao grupo
			group.addUser(username);
			
			//atualizar ficheiro
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			
			objectOutputStream.writeObject(group);
			
			objectOutputStream.close();
			fileOutputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
