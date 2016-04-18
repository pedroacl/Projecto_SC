package network.messages;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class ChatMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2504265010288468745L;

	// assinatura da mensagem
	private byte[] signature;

	// mensagem cifrada
	private byte[] message;
	
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

	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	public void setFromUser(String username) {
		this.fromUser = username;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
}
