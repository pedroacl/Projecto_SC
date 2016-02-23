import interfaces.ServerThreadInterface;

public class ServerThread extends Thread implements ServerThreadInterface {
	
	private Request clientRequest;
	
	public ServerThread(Request clientRequest) {
		this.clientRequest = clientRequest; 
	}

	public void run() {
		switch (clientRequest.getRequestType()) {
		case ADDUSER:	
			
			break;
			
		case REMOVEUSER:
			
			break;
			
		case AUTH:
			
			break;
			
		case ERR:
			
			break;
			
		case FILE:
			
			break;
			
		case MESSAGE:
			
			break;

		default:
			break;
		}
	}
}
