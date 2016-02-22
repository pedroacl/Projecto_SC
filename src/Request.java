
public class Request {
	
	public Request() {
		
	}
	
	private enum RequestType {
		AUTH, MESSAGE, FILE, ADDUSER, REMOVEUSER, ERR;
	}
	
	private RequestType opCode;
	
	private String message;
	
	private String fromUser;
	
	private String toUser;
	
	
	public String getMessage() {
		opCode = RequestType.AUTH;
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public RequestType getOpCode() {
		return opCode;
	}

	public void setOpCode(RequestType opCode) {
		this.opCode = opCode;
	}
}
