package network.messages;

import java.util.ArrayList;
import java.util.List;

public class ServerNetworkContactTypeMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8328367165489345591L;

	private List<String> groupMembers;

	public ServerNetworkContactTypeMessage(MessageType messageType) {
		super(messageType);
		groupMembers = new ArrayList<>();
	}

	public void addGroupMember(String username) {
		groupMembers.add(username);
	}
	
	public int numGroupMembers() {
		return groupMembers.size();
	}
	
	public List<String> getGroupMembers() {
		return groupMembers;
	}
}
