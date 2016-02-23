package factories;

import entities.Conversation;

public class ConversationFactory {
	
	private Long conversationId;
	
	public ConversationFactory() {
		conversationId = 0L;
	}
	
	public Conversation build() {
		Conversation conversation = new Conversation();
		conversation.setId(conversationId + 1);
		
		return conversation;
	}
}
