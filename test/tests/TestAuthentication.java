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
		
	}
}
