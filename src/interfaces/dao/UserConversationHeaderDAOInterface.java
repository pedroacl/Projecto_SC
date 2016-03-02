package interfaces.dao;

import entities.Conversation;
import entities.UserConversationHeader;

public interface UserConversationHeaderDAOInterface {
	
	public void addUsersConversationHeaders(Conversation conversation);
	
	public UserConversationHeader getUserConversationHeader(String username, String toUser);
	
	public Long getConversationId(String fromUser, String toUser);

}
