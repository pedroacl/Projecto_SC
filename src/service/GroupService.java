package service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import dao.GroupDAO;
import entities.Group;
import security.SecurityUtils;

/**
 * 
 * @author pedro
 *
 */
public class GroupService {

	private GroupDAO							groupDAO;

	private static ConversationService			conversationService;

	private static GroupService					groupService	= new GroupService();

	private ConcurrentHashMap<String, String>	groups;									// groupName:owner
	
	private GroupService() {
		groupDAO = new GroupDAO();
		groups = groupDAO.getGroups();
		conversationService = new ConversationService();
	}

	/**
	 * 
	 * @return
	 */
	public static GroupService getInstance() {
		return groupService;
	}

	/**
	 * 
	 * @param username
	 * @param userToAdd
	 * @param groupName
	 */
	public boolean addUserToGroup(String username, String userToAdd, String groupName) {
		
		if (existsGroup(groupName) && getGroupOwner(groupName).equals(username)) {
			// ler ficheiro
			Group group = groupDAO.getGroup(groupName);

			// utilizador nao adicionado ao grupo
			if (!group.getUsers().contains(userToAdd)) {
				groupDAO.addUserToGroup(userToAdd, groupName);

				// adicionar id da conversa do grupo no ficheiro de conversas do
				// user
				conversationService.addConversationToUser(userToAdd, groupName, group.getConversationId());

				return true;
			}
		}
		// grupo nao existe
		else {

			if (!existsGroup(groupName)) {
				Long conversationId = groupDAO.createGroup(groupName, username);
				groupDAO.addUserToGroup(userToAdd, groupName);
				conversationService.addConversationToUser(username, groupName, conversationId);
				conversationService.addConversationToUser(userToAdd, groupName, conversationId);
				groups.put(groupName, username);

				return true;
			}
		}

		return false;
	}

	/**
	 * Method to remove a user from a group
	 * 
	 * @param username
	 *            - user that send the message to delete the user
	 * @param userToRemove
	 *            user to be removed from the group
	 * @param groupName
	 *            name of the group
	 * @return
	 */
	public boolean removeUserFromGroup(String username, String userToRemove, String groupName) {

		// existe grupo e o utilizador eh owner
		if (existsGroup(groupName) && getGroupOwner(groupName).equals(username)) {

			// ler ficheiro
			Group group = groupDAO.getGroup(groupName);

			// caso user seja o dono do grupo
			// apagar grupo inteiro
			if (getGroupOwner(groupName).equals(userToRemove)) {
				// Apaga a informaçao da conversa correspondente ao grupo em
				// cada elemento
				ArrayList<String> members = (ArrayList<String>) group.getUsers();

				for (String user : members)
					conversationService.removeConversationFromUser(user, groupName);

				// Apaga conversa da pasta conversations
				conversationService.removeConversation(group.getConversationId());

				// Apaga o group do "disco"
				groupDAO.deleteGroup(groupName);
				groups.remove(groupName);

				return true;
			}
			// apaga membro do grupo
			else {
				if (group.contains(userToRemove)) {
					// remove a conversa do grupo das suas conversaçoes
					conversationService.removeConversationFromUser(userToRemove, groupName);

					// remove the key of the user for that group
					conversationService.removeKeyUserFromFolder(userToRemove, group.getConversationId());

					// remove utilizador do grupo e persiste informação
					groupDAO.removeUserFromGroup(group, userToRemove);
					return true;
				}
			}
		}
		return false;
	}

	public boolean existsGroup(String groupName) {
		return groups.get(groupName) != null;
	}

	/**
	 * 
	 * @param groupName
	 * @return
	 */
	public String getGroupOwner(String groupName) {
		return groups.get(groupName);
	}

	/**
	 * 
	 */
	public Long createGroup(String groupName, String admin) {
		groups.put(groupName, admin);
		return groupDAO.createGroup(groupName, admin);
	}

	public List<String> getGroupMembers(String groupName) {
		Group grupo = groupDAO.getGroup(groupName);
		return grupo.getUsers();
	}
}
