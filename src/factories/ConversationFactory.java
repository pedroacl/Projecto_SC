package factories;

import entities.Conversation;

public class ConversationFactory {
	private static Long conversationId;
	
	public ConversationFactory() {
		conversationId = 0L;
	}
	
	public Conversation build(String fromUser, String toUser) {
		Conversation conversation = new Conversation(fromUser, toUser);
		conversation.setId(conversationId++);
		
		return conversation;
	}
}
