package factories;

import java.util.Date;

import entities.UserMessage;

public class MessageFactory {

	private static Long id;
	
	public UserMessage build(String fromUser, String toUser) {
		UserMessage message = new UserMessage(fromUser, toUser);
		
		message.setId(id + 1);
		message.setCreatedAt(new Date());
		
		return message;
	}
}
