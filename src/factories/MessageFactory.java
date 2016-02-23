package factories;

import java.util.Date;

import entities.Message;

public class MessageFactory {

	private static Long id;
	
	public Message build(String fromUser, String toUser) {
		Message message = new Message(fromUser, toUser);
		
		message.setId(id + 1);
		message.setCreatedAt(new Date());
		
		return message;
	}
}
