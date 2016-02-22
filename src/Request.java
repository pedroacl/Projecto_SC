
public class Request {
	
	public Request() {
		
	}
	
	private RequestType requestType;
	
	private String message;
	
	private String fromUser;
	
	private String toUser;
	
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
}
