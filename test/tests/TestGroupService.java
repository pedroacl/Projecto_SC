package tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import domain.server.Authentication;
import entities.Group;
import service.GroupService;
import util.PersistenceUtil;

public class TestGroupService {

	private Authentication authentication;

	private GroupService groupService;

	@Before
	public void setUp() {
		try {
			FileUtils.deleteDirectory(new File("users"));
			FileUtils.deleteDirectory(new File("conversations"));

			File file = new File("users.txt");

			if (file.exists())
				FileUtils.forceDelete(file);

			file = new File("groups.txt");

			if (file.exists())
				FileUtils.forceDelete(file);

		} catch (IOException e) {
			e.printStackTrace();
		}

		authentication = Authentication.getInstance();
		authentication.addUser("antonio", "1234");
		authentication.addUser("jose", "4321");

		groupService = GroupService.getInstance();
	}

	@Test
	public void testCreateGroup() {
		String groupName = "grupo";
		String admin = "maria";

		Long groupId = groupService.createGroup(groupName, admin);
		assertThat(groupId, is(not(nullValue())));

		File file = new File("groups/" + groupName);
		assertThat(file.exists(), is(true));

		file = new File("groups/" + groupName + "/group");
		assertThat(file.exists(), is(true));

		file = new File("conversations/" + groupId);
		assertThat(file.exists(), is(true));

		file = new File("conversations/" + groupId + "/messages");
		assertThat(file.exists(), is(true));
	}

	@Test
	public void testAddUserToGroup() {
		String groupName = "grupo";
		String userToAdd = "pedro";
		String admin = "maria";

		Long conversationId = groupService.createGroup(groupName, admin);
		System.out.println(conversationId);

		groupService.addUserToGroup(admin, userToAdd, groupName);

		File file = new File("users.txt");
		assertThat(file.exists(), is(true));

		Group group = (Group) PersistenceUtil.readObject("groups/" + groupName + "/group");
		assertThat(group.getUsers().contains(admin), is(true));
		assertThat(group.getUsers().contains(userToAdd), is(true));

		// user 1
		file = new File("users/" + admin + "/conversations");
		assertThat(file.exists(), is(true));

		HashMap<String, Long> userConversations = (HashMap<String, Long>) PersistenceUtil
				.readObject("users/" + admin + "/conversations");

		assertThat(userConversations, is(not(nullValue())));
		assertThat(userConversations.get(groupName), is(conversationId));

		// user 2
		file = new File("users/" + admin + "/conversations");
		assertThat(file.exists(), is(true));

		userConversations = (HashMap<String, Long>) PersistenceUtil.readObject("users/" + userToAdd + "/conversations");

		assertThat(userConversations, is(not(nullValue())));
		assertThat(userConversations.get(groupName), is(conversationId));

	}
}
