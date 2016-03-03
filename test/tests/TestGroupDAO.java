package tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dao.GroupDAO;
import domain.Authentication;

public class TestGroupDAO {

	private static Authentication authentication;
	
	private static GroupDAO groupDAO;

	@Before
	public void setUp() {
		authentication = Authentication.getInstance();
		groupDAO = GroupDAO.getInstance();

		try {
			FileUtils.deleteDirectory(new File("users"));
			FileUtils.deleteDirectory(new File("conversations"));

			File file = new File("users.txt");

			if (file.exists()) {
				FileUtils.forceDelete(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		authentication.addUser("antonio", "my_password");
	}

	@After
	public void tearDown() {

	}
	
	@Test
	public void testGetGroups(){
		groupDAO.get
	}

	@Test
	public void testGetGroupByName() {
		
	}
	
	@Test
	public void testGetGroupId() {
		
	}

	@Test
	public void testAddUserToGroup() {
		
	}
}
