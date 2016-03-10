package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	
	
	private void send(ServerMessage message) {
		try {
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(ServerMessage message) {
		if(message.getMessageType().equals(MessageType.FILE))
			sendFile(message);
		else
			send(message);
	}
	
	public boolean sendFile(ServerMessage message) {
		boolean isValid = false;
		
		String filePath = message.getContent();
		message.setContent(extractName(filePath));
		
		send(message);
		
		//ClientMessage cm = getClientMessage();
		
		if(true) {//cm.getMessageType().equals(MessageType.OK)) {
			isValid = true;
			try {
				sendByteFile(filePath, message.getFileSize());
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
		
		System.out.println("[SERVERSOCKETNETWORK]- sendByteFile: File " + name);
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
		out.flush();
		fileInputStream.close();	
	}
	
	public File receiveFile( int sizeFile, String name ) {
		File file = new File(name);
		FileOutputStream fileOut = null;

		try {
			fileOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("O tamnho do ficheiro é :" + sizeFile);

		int packageSize = 1024;
		int currentLength = 0;
		byte [] bfile = new byte [packageSize];
		int lido;

		while(currentLength < sizeFile) {
			int resto = sizeFile-currentLength;
			int numThisTime = resto < packageSize ? resto : bfile.length;
			
			try {
				lido = in.read(bfile, 0, numThisTime);

				if(lido == -1) {
					break;
				}

				System.out.println("li: "+ lido );	
				
				fileOut.write(bfile,0,numThisTime);
				currentLength += lido;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		try {
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("li no total2: "+ currentLength );
		
		return file;
					
	}
	
	private String extractName(String absolutePath) {
		String [] splitName = absolutePath.split("/");
		return splitName[splitName.length - 1] ;
	}
}
