package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domain.server.Authentication;

public class TestAuthentication {

	private Authentication authentication;

	@Before
	public void setUp() {

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
