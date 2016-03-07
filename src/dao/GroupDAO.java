package dao;

import java.io.File;
import java.util.HashMap;

import entities.Group;
import factories.ConversationFactory;
import factories.GroupFactory;
import interfaces.dao.GroupDAOInterface;
import util.MiscUtil;

public class GroupDAO implements GroupDAOInterface {

	private static GroupDAO groupDAO = new GroupDAO();
	
	private static ConversationFactory conversationFactory;

	private GroupDAO() {
		conversationFactory = conversationFactory.getInstance();
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
		//cria directoria groups se não existir ainda
		MiscUtil.createDir("groups/groups");
		
		//cria group
		long conversationId = conversationFactory.generateID();
		Group novoGrupo = new Group(groupName, admin, conversationId);
		
		//Persiste grupo na directoria groups
		MiscUtil.createDir("groups/" + groupName);
		MiscUtil.writeObject(novoGrupo, "groups/" + groupName + "/group");
		
		//Cria directoria da convresaçao com o respectivo id na pasta de conversaçoes
		MiscUtil.createDir("conversations/" + conversationId);
		MiscUtil.createDir("conversations/" + conversationId + "/messages");
		MiscUtil.createFile("conversations/" + conversationId + "/conversation");
		
		return conversationId;
	}
}
