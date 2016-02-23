package factories;

import java.io.File;

import entities.Conversation;

public class ConversationFactory {
	
	private Long conversationId;
	
	public ConversationFactory() {
		conversationId = 0L;
	}
	
	public Conversation build() {
		Conversation conversation = new Conversation();
		conversation.setId(conversationId + 1);
		
		//criar pasta para a conversa
		File file = new File("conversations/");
		
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directorio criado");
			} else {
				System.out.println("Directorio nao criado");
			}
		}
		
		return conversation;
	}
}
