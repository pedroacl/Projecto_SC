package tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domain.Authentication;

public class TestAuthentication {

	private Authentication authentication;

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
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testAuthenticatedUser() {
		
	}
}
