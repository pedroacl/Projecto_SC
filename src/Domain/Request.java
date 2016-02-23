package Domain;

import java.io.Serializable;



public class Request implements Serializable {
	
	
	private RequestType requestType;
	
	private String message;
	
	private byte [] file;
	
	private String fromUser;
	
	private String toContact;
	
	public Request( String from, RequestType type ) {
		this.fromUser = from;
		requestType = type;
		
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setFileByte(byte [] file) {
		this.file = file;
	}

	public RequestType getRequestType() {
		return requestType;
	}
	
	public void setToContact(String toConctat) {
		this.toContact = toContact;
	}

}
