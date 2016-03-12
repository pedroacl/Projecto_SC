package service;

import java.util.ArrayList;
import java.util.HashMap;

import dao.GroupDAO;
import entities.Group;
import interfaces.service.GroupServiceInterface;
import util.MiscUtil;

public class GroupService implements GroupServiceInterface {

	private GroupDAO groupDAO;

	private static ConversationService conversationService;

	private HashMap<String, String> groups; // groupName:owner

	public GroupService() {
		groupDAO = new GroupDAO();
		groups = groupDAO.getGroups();
		conversationService = new ConversationService();
	}

	/**
	 * 
	 * @param username
	 * @param userToAdd
	 * @param groupName
	 */
	@Override
	public boolean addUserToGroup(String username, String userToAdd, String groupName) {

		if (existsGroup(groupName) && getGroupOwner(groupName).equals(username)) {
			// ler ficheiro
			String filePath = "groups/" + groupName + "/group";
			Group group = (Group) MiscUtil.readObject(filePath);
			System.out.println("[GroupService]" + group.getConversationId());

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
			Long conversationId = groupDAO.createGroup(groupName, username);
			groupDAO.addUserToGroup(userToAdd, groupName);
			conversationService.addConversationToUser(username, groupName, conversationId);
			conversationService.addConversationToUser(userToAdd, groupName, conversationId);
			groups.put(groupName, username);

			return true;
		}

		return false;
	}

	@Override
	public boolean removeUserFromGroup(String username, String userToRemove, String groupName) {
		
		// existe grupo e o utilizador eh owner
		if (existsGroup(groupName) && getGroupOwner(groupName).equals(username)) {
			// ler ficheiro
			String filePath = "groups/" + groupName + "/group";
			Group group = (Group) MiscUtil.readObject(filePath);

			// caso user seja o dono do grupo
			// apagar grupo inteiro
			if (getGroupOwner(groupName).equals(userToRemove)) {
				// Apaga a informaçao da conversa correspondente ao grupo em
				// cada elemento
				ArrayList<String> members = (ArrayList<String>) group.getUsers();

				for (String user : members) {
					conversationService.removeConversationFromUser(user, groupName);
				}

				// Apaga conversa da pasta conversations
				conversationService.removeConversation(group.getConversationId());

				// Apaga o group do "disco"
				groupDAO.deleteGroup(groupName);
				groups.remove(groupName);
				
				return true;
			}
			// apaga membro do grupo
			else {
				conversationService.removeConversationFromUser(userToRemove, groupName);
				return true;
			}
		}

		return false;
	}
	
	@Override
	public boolean existsGroup(String groupName) {
		return groups.get(groupName) != null;
	}

	/**
	 * 
	 * @param groupName
	 * @return
	 */
	@Override
	public String getGroupOwner(String groupName) {
		return groups.get(groupName);
	}

	/**
	 * 
	 */
	@Override
	public Long createGroup(String groupName, String admin) {
		groups.put(groupName, admin);
		return groupDAO.createGroup(groupName, admin);
	}
}
