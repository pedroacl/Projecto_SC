package network.messages;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class ChatMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2504265010288468745L;

	private byte[] cypheredMessage;

	private byte[] cypheredMessageKey;

	// assinatura da mensagem
	private byte[] signature;

	private String fromUser;

	private String destination;

	// username->chave secreta cifrada com a sua chave publica
	private HashMap<String, byte[]> users;

	private Date createdAt;

	public ChatMessage(MessageType chatMessage) {
		super(chatMessage);
		users = new HashMap<>();
		createdAt = new Date();
	}

	public byte[] getCypheredMessageKey() {
		return cypheredMessageKey;
	}

	public void setCypheredMessageKey(byte[] cypheredMessageKey) {
		this.cypheredMessageKey = cypheredMessageKey;
	}

	public byte[] getCypheredMessage() {
		return cypheredMessage;
	}

	public void setCypheredMessage(byte[] cypheredMessage) {
		this.cypheredMessage = cypheredMessage;
	}

	public String getDestination() {
		return destination;
	}

	public String getFromUser() {
		return fromUser;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Set<String> getUsers() {
		return users.keySet();
	}

	public byte[] getUserKey(String username) {
		return users.get(username);
	}

	public void putUserKey(String user, byte[] key) {
		users.put(user, key);
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public void setFromUser(String username) {
		this.fromUser = username;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
}
