package tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domain.Authentication;
import entities.User;

public class TestAuthentication {

	private Authentication authentication;

	@Before
	public void setUp() {
		try {
			FileUtils.deleteDirectory(new File("users"));
			FileUtils.deleteDirectory(new File("conversations"));
			FileUtils.forceDelete(new File("users.txt"));

		} catch (IOException e) {
			e.printStackTrace();
		}

		authentication = Authentication.getInstance();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testAuthenticatedUser() {
		User user = authentication.getUser("pedro");
		assertThat(user, is(not(nullValue())));
	}

	@Test
	public void testAddUser() {
		String username1 = "pedro";
		String username2 = "jose";

		// adicionar primeiro user
		User user1 = authentication.getUser(username1);
		assertThat("Utilizador ja existe", user1, is(nullValue()));

		authentication.addUser(username1, "1234");
		user1 = authentication.getUser(username1);
		assertThat("User nao existe", user1, is(not(nullValue())));

		// adicionar segundo user
		User user2 = authentication.getUser(username2);
		assertThat("Utilizador ja existe", user2, is(nullValue()));

		authentication.addUser(username2, "1234");
		user2 = authentication.getUser(username2);
		assertThat("User nao existe", user2, is(not(nullValue())));

		File file = new File("users.txt");
		assertThat(file.exists(), is(true));

		file = new File("users/pedro");
		assertThat("Ficheiro nao existe", file.exists(), is(true));

		file = new File("users/pedro/conversations");
		assertThat("Ficheiro nao existe", file.exists(), is(true));
	}
}
