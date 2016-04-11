package network.messages;

import java.util.ArrayList;
import java.util.List;

public class ServerContactTypeMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8328367165489345591L;

	private List<String> groupMembers;

	public ServerContactTypeMessage(MessageType messageType) {
		super(messageType);
	}

	public ServerContactTypeMessage(MessageType messageType, ArrayList<String> groupMembers) {
		super(messageType);
		this.groupMembers = groupMembers;
	}
	
	public List<String> getGroupMembers() {
		return groupMembers;
	}
}
