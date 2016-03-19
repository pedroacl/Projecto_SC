package tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dao.GroupDAO;
import domain.server.Authentication;

public class TestGroupDAO {

	private static Authentication authentication;
	
	private static GroupDAO groupDAO;

	@Before
	public void setUp() {
		authentication = Authentication.getInstance();

		groupDAO = new GroupDAO();
		
		authentication.addUser("antonio", "1234");
	}

	@After
	public void tearDown() {

	}
	
	@Test
	public void testCreateGroup() {
		String groupName = "grupo1";
		String admin = "maria";

/*		Long conversationId = authentication.addGroup(groupName, admin);

		File file = new File("groups/" + groupName);
		assertThat(file.exists(), is(true));
		
		file = new File("conversations/" + conversationId);
		assertThat(file.exists(), is(true));
		*/
	}
	
	@Test
	public void testGetGroups(){
		String groupName = "grupo1";
		String user = "jose";
	
		groupDAO.addUserToGroup(groupName, "pedro");
		groupDAO.addUserToGroup(groupName, "maria");

		File file = new File("groups/groups");
		assertThat(file.exists(), is(true));

		file = new File("groups/1");
		assertThat(file.exists(), is(true));
		assertThat(file.isDirectory(), is(true));
	}
	
	@Test
	public void testGetGroupId() {
		String groupName = "grupo1";
		String user = "jose";
	
		groupDAO.createGroup(groupName, user);
	}

	@Test
	public void testAddUserToGroup() {
		String user1 = "jose";
		String user2 = "pedro";
		String groupName = "grupo1";

		groupDAO.addUserToGroup(groupName, user2);
		//assertThat(group.getUsers().size(), is(2));
	}
}
