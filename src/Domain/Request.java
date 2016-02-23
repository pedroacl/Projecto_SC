package Domain;


import java.io.Serializable;



public class Request implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8053113085617628635L;


	public Request() {
		
	}

	public Request( String from, RequestType type ) {
		this.fromUser = from;
		requestType = type;
		
	}
	
	private RequestType requestType;
	
	private String message;

	private byte [] file;
	
	private String fromUser;
	
	private String toUser;


	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}


	private String toContact;
	
	
	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
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
