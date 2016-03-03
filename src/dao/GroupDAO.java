package dao;

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
	public void addUserToGroup(Group group, String username) {
		
		MiscUtil.createDir("groups/" + group.getId());
		MiscUtil.createFile("groups/groups");
		MiscUtil.createFile("groups/" + group.getId() + "/group");

		HashMap<String, Group> groups = getGroups();
		
		if (groups == null)
			groups = new HashMap<String, Group>();
		
		// atualizar dados
		group.addUser(username);
		groups.put(group.getName(), group);
		MiscUtil.writeObject(groups, "groups/groups");
	}

	/**
	 * Função que permite obter o ID de um determinado grupo
	 */
	@Override
	public Long getGroupId(String groupName) {
		HashMap<String, Group> groups = getGroups();

		if (groups != null)
			return null;

		Group group = groups.get(groupName);

		if (group == null)
			return null;

		return group.getId();
	}

	/**
	 * Função que permite obter um hashmap com todos os grupos registados
	 */
	@Override
	public HashMap<String, Group> getGroups() {
		return (HashMap<String, Group>) MiscUtil.readObject("groups/groups");
	}

	/**
	 * Função que permite criar um grupo
	 */
	@Override
	public Long createGroup(String groupName, String admin) {
		HashMap<String, Group> groups = getGroups();
		Group group = null;

		//nao existem grupos adicionados
		if (groups == null) {
			groups = new HashMap<String, Group>();
			group = groupFactory.build(groupName, admin);
			groups.put(groupName, group);

		} else {
			group = groups.get(groupName);

			if (group == null) {
				group = groupFactory.build(groupName, admin);
				groups.put(groupName, group);
			} else {
				return group.getId();
			}
		}
		
		MiscUtil.createFile("groups/groups");
		MiscUtil.createDir("groups/" + group.getId());
		MiscUtil.writeObject(groups, "groups/groups");
		
		return group.getId();
	}
}
