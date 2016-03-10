package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import dao.ConversationDAO;
import dao.GroupDAO;
import domain.Authentication;
import factories.ConversationFactory;

public class TestGroupService {

	@Before
	public void setUp() {
		try {
			FileUtils.deleteDirectory(new File("users"));
			FileUtils.deleteDirectory(new File("conversations"));
			FileUtils.deleteDirectory(new File("groups"));

			File file = new File("users.txt");

			if (file.exists()) {
				FileUtils.forceDelete(file);
			}
			
			file = new File("groups.txt");

			if (file.exists())
				FileUtils.forceDelete(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		conversationFactory = ConversationFactory.getInstance();
		conversationFactory.reset();

		conversationDAO = ConversationDAO.getInstance();
		groupDAO = GroupDAO.getInstance();
		authentication = Authentication.getInstance();

		authentication.addUser("antonio", "my_password");
		*/
	}
	
	@Test
	public void 

}
