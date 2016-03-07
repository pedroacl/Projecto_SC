package dao;

import java.io.File;
import java.util.HashMap;

import entities.Group;
import factories.GroupFactory;
import interfaces.dao.GroupDAOInterface;
import util.MiscUtil;

public class GroupDAO implements GroupDAOInterface {

	private static GroupDAO groupDAO = new GroupDAO();
	
	private static GroupFactory groupFactory;

	private GroupDAO() {
		groupFactory = GroupFactory.getInstance();
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
		//TODO

		return null;
	}

	/**
	 * Adicionar utilizador a um grupo
	 * 
	 * @param groupName
	 * @param username
	 */
	@Override
	public boolean addUserToGroup(String username, String groupName) {
		String filePath = "groups/" + groupName + "/group";
		
		Group group = (Group) MiscUtil.readObject(filePath);
		
		if (group.addUser(username)){
			MiscUtil.writeObject(group, filePath);
		} else {
			return false;
		}
		
		return true;
	}

	/**
	 * Função que permite obter um hashmap com todos os grupos registados
	 */
	@Override
	public HashMap<String, String> getGroups() {
		//TODO
		return null;
	}

	/**
	 * Função que permite criar um grupo
	 */
	@Override
	public Long createGroup(String groupName, String admin) {
		MiscUtil.createFile("groups/groups");
	
		//TODO

		return null;
	}
}
