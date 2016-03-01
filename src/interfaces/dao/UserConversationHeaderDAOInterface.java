package interfaces.dao;

import entities.Conversation;
import entities.ConversationHeader;

public interface UserConversationHeaderDAOInterface {
	
	public ConversationHeader getUserConversationHeader(String username, String toUser);

	public void addUserConversationHeader(String username, Conversation conversation);

}
