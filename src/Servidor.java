import java.io.IOException;
import java.net.ServerSocket;

public class Servidor {
	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
