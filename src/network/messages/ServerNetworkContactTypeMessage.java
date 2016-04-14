package network.messages;

import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Set;

public class ServerNetworkContactTypeMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8328367165489345591L;

	private HashMap<String, Certificate> groupMembers;

	public ServerNetworkContactTypeMessage(MessageType messageType) {
		super(messageType);
		groupMembers = new HashMap<>();
	}

	public void addGroupMember(String username, Certificate certificate) {
		groupMembers.put(username, certificate);
	}
	
	public Certificate getGroupMember(String username) {
		return groupMembers.get(username);
	}
	
	public int numGroupMembers() {
		return groupMembers.size();
	}
	
	public Set<String> getGroupMembers() {
		return groupMembers.keySet();
	}
	
	public Certificate getCertificate(String username) {
		return groupMembers.get(username);
	}
}
