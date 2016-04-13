package network.messages;

import java.util.HashMap;

public class ClientPGPMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2504265010288468745L;

	private byte[] signature;

	private byte[] message;

	private HashMap<String, byte[]> userKey;

	public ClientPGPMessage() {

	}

	public ClientPGPMessage(byte[] signature, byte[] message) {
		this.signature = signature;
		this.message = message;
		userKey = new HashMap<>();
	}
	
	public void putUserKey(String user, byte[] key) {
		userKey.put(user, key);
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
}
