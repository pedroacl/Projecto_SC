package tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domain.Authentication;
import entities.User;
import util.MiscUtil;

public class TestAuthentication {
	
	private Authentication authentication;
	
	@Before
    public void setUp() {
		authentication = Authentication.getInstance();

		MiscUtil.delete(new File("users"));
		MiscUtil.delete(new File("conversations"));
		MiscUtil.delete(new File("users.txt"));
    }
	
    @After
    public void tearDown() {
		MiscUtil.delete(new File("users"));
		MiscUtil.delete(new File("conversations"));
    }

    
    @Test
    public void testAuthenticatedUser() {
		User user = authentication.getUser("pedro");
    	assertThat(user, is(not(nullValue())));
    }

	
	@Test
	public void testAddUser() {
		File file = new File("users");
		assertThat(file.exists(), is(false));
		
		User user = authentication.getUser("pedro");
		assertThat("Utilizador ja existe", user, is(nullValue()));

		authentication.addUser("pedro", "1234");
		user = authentication.getUser("pedro");
		assertThat("User nao existe", user, is(not(nullValue())));

		file = new File("users/pedro");
		assertThat("Ficheiro nao existe", file.exists(), is(true));
		
		file = new File("users/pedro/conversations");
		assertThat("Ficheiro nao existe", file.exists(), is(true));
	}
}
