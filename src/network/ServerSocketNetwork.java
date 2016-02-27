package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.Socket;

public class ServerSocketNetwork {
	
	private Socket socket;
	
	private ObjectInputStream in;
	
	private ObjectOutputStream out;


	public ServerSocketNetwork(Socket socket) {
		this.socket = socket;
		
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void close(){
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public ClientMessage getClientMessage() {
		ClientMessage clientMessage = null;

		try {
			clientMessage = (ClientMessage) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return clientMessage;
	}
	
	
	private void send(ClientMessage message) {
		try {
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(ClientMessage message) {
		if(message.getMessageType().equals(MessageType.FILE))
			sendFile(message);
		else
			send(message);
	}
	
	public boolean sendFile(ClientMessage message) {
		boolean isValid = false;
		
		send(message);
		
		ClientMessage cm = getClientMessage();
		
		if(cm.getMessageType().equals(MessageType.OK)) {
			isValid = true;
			try {
				sendByteFile(message.getMessage(), message.getFileSize());
			} catch (IOException e) {
				e.printStackTrace();
				isValid = false;
			}
			
		}
		else
			isValid = false;
		
		return isValid;
	}
	
	private void sendByteFile(String name,int fileSize) throws IOException {
		int packageSize = 1024;
		
		FileInputStream fileInputStream = new FileInputStream(name);
		int currentLength = 0;
		byte [] bfile;
		while(currentLength < fileSize) {
			if((fileSize - currentLength) < packageSize)
				bfile= new byte[(fileSize - currentLength)];
			else
				bfile = new byte[packageSize];
			int lido = fileInputStream.read(bfile,0,bfile.length);
			currentLength += lido;
			out.write(bfile,0,bfile.length);
		}
		fileInputStream.close();	
	}
	
	public File receiveFile( int sizeFile, String name ) throws IOException {
		File file = new File(name);
		FileOutputStream fileOut =  new FileOutputStream(file);
		int packageSize = 1024;
		int currentLength = 0;
		byte [] bfile;
		int lido;
		while(currentLength < sizeFile) {
			if((sizeFile-currentLength) < packageSize)
				bfile= new byte [sizeFile-currentLength];
			else
				bfile= new byte [packageSize];
			lido = in.read(bfile, 0, bfile.length);
			fileOut.write(bfile);
			currentLength += lido;
		}
		
		fileOut.close();
		
		return file;
					
	}
}
