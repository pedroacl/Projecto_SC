
public class Request {
	
	public Request() {
		
	}
	
	private enum RequestType {
		AUTH, MESSAGE, FILE, ADDUSER, REMOVEUSER;
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

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
}
