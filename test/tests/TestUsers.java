package tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import util.MiscUtil;

public class TestUsers {

	@Before
	public void setUp() {
		
	}

	@Test
	public void testGetUsersConversations() {
		String user1 = "pedro";

		File file = new File("users/" + user1 + "/conversations");
		assertThat(file.exists(), is(true));
		
		HashMap<String, String> conversations = (HashMap<String, String>) MiscUtil
				.readObject("users/" + user1 + "/conversations");
		
		assertThat(conversations, is(not(nullValue())));
		System.out.println(conversations);
	}
}