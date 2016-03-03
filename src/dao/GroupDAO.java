package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import entities.Group;
import interfaces.dao.GroupDAOInterface;
import util.MiscUtil;

public class GroupDAO implements GroupDAOInterface {

	private static GroupDAO groupDAO = new GroupDAO();

	private GroupDAO() {

	}

	public static GroupDAO getInstance() {
		return groupDAO;
	}

	/**
	 * 
	 * @param groupName
	 * @return
	 */
	@Override
	public Group getGroupByName(String groupName) {
		HashMap<String, Group> groups = (HashMap<String, Group>) MiscUtil.readObject("groups/groups");
		
		if (groups != null) {
			return groups.get(groupName);
		}

		return null;
	}

	/**
	 * Adicionar utilizador a um grupo
	 * 
	 * @param groupName
	 * @param username
	 */
	@Override
	public void addUserToGroup(String groupName, String username) {
		HashMap<String, Group> groups = getGroups();
		
		MiscUtil.createFile("groups/groups");
		MiscUtil.createFile("groups/" + groupName + "/group");
		
		Group group = null;

		group = getGroupByName(groupName);

		// utilizador já adicionado ao grupo
		if (group.getUsers().contains(username))
			return;

		// adicionar utilizador ao grupo
		group.addUser(username);

		// atualizar ficheiro
		MiscUtil.writeObject(group, "groups/groups");
	}

	@Override
	public Long getGroupId(String groupName) {
		File file = new File("groups/groups");

		if (!file.exists())
			return null;

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

			HashMap<String, Group> groups = (HashMap<String, Group>) objectInputStream.readObject();

			if (groups == null)
				return null;

			Group group = groups.get(groupName);

			return group.getId();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public HashMap<String, Group> getGroups() {
		File file = new File("groups/groups");
		HashMap<String, Group> groups = null;	
		
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			
			groups = (HashMap<String, Group>) objectInputStream.readObject();
			
			objectInputStream.close();
			fileInputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return groups;
	}
}
