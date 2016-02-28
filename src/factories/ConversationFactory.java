package factories;

import entities.Conversation;

public class ConversationFactory {
	private static Long conversationId;
	
	private static ConversationFactory conversationFactory = new ConversationFactory();
	
	
	private ConversationFactory() {
		conversationId = 0L;
	}
	
	
	public static ConversationFactory getInstance() {
		return conversationFactory;
	}
	
	
	public Conversation build(String fromUser, String toUser) {
		Conversation conversation = new Conversation(fromUser, toUser);
		conversation.setId(conversationId++);
		
		return conversation;
	}
}
