package service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import dao.GroupDAO;
import entities.Group;
import exceptions.InvalidMacException;

/**
 * 
 * @author pedro
 *
 */
public class GroupService {

	private GroupDAO groupDAO;

	private static ConversationService conversationService;

	private ConcurrentHashMap<String, String> groups; // groupName:owner
	
	private String serverPassword;

	public GroupService(String serverPassword) {
		this.serverPassword = serverPassword;
		groupDAO = new GroupDAO(serverPassword);

		try {
			groups = groupDAO.getGroups();
		} catch (InvalidMacException e) {
			e.printStackTrace();
		}

		conversationService = new ConversationService();
	}

	/**
	 * 
	 * @param username
	 * @param userToAdd
	 * @param groupName
	 * @throws InvalidMacException 
	 */
	public boolean addUserToGroup(String username, String userToAdd, String groupName) throws InvalidMacException {

		if (existsGroup(groupName) && getGroupOwner(groupName).equals(username)) {
			// ler ficheiro
			Group group = groupDAO.getGroup(groupName);

			System.out.println("[GROUPOSERVICE] addUserToGroup:"+  userToAdd);
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
				try {
					groupDAO.deleteGroup(groupName);
				} catch (InvalidMacException e) {
					e.printStackTrace();
				}
				
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
	 * @param groupName
	 * @param admin
	 * @return
	 */
	public Long createGroup(String groupName, String admin) {
		groups.put(groupName, admin);
		Long groupId = null;

		try {
			groupId = groupDAO.createGroup(groupName, admin);
		} catch (InvalidMacException e) {
			e.printStackTrace();
		}
		
		return groupId;
	}

	/**
	 * 
	 * @param groupName
	 * @return
	 */
	public List<String> getGroupMembers(String groupName) {
		Group grupo = groupDAO.getGroup(groupName);
		return grupo.getUsers();
	}
}
