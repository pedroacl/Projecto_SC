package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domain.Authentication;
import entities.User;

public class TestAuthentication {
	
	private Authentication authentication;
	
	@Before
    public void setUp() {
		authentication = Authentication.getInstance();
    }
	
    @After
    public void tearDown() {

    }
	
	@Test
	public void testAddUser() {
		
		User user = authentication.getUser("pedro");
		assertThat(user, is(nullValue()));

		authentication.addUser("pedro", "1234");

		File file = new File("users/pedro");
		assertEquals(file.exists(), true);
		
		file = new File("users/pedro/conversations");
		assertEquals(file.exists(), true);
	}
	
	
	void delete(File f) {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
	}
}
