package network.messages;

import java.util.List;

public class ClientPGPMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2504265010288468745L;

	private byte[] signature;

	private byte[] message;

	private List<byte[]> wrappedSecretKeys;

	public ClientPGPMessage() {

	}

	public ClientPGPMessage(byte[] signature, byte[] message, List<byte[]> wrappedSecretKeys) {
		this.signature = signature;
		this.message = message;
		this.wrappedSecretKeys = wrappedSecretKeys;
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

	public List<byte[]> getWrappedSecretKeys() {
		return wrappedSecretKeys;
	}

	public void setWrappedSecretKey(List<byte[]> wrappedSecretKeys) {
		this.wrappedSecretKeys = wrappedSecretKeys;
	}

	public void addWrappedSecretKey(byte[] wrappedSecretKey) {
		wrappedSecretKeys.add(wrappedSecretKey);
	}

}
