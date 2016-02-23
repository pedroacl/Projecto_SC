package factories;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import Domain.Request;
import Domain.RequestType;

public class RequestFactory {
	
	public RequestFactory() {};
	
	public Request build(String from, String accao) {
		
		String [] splited = accao.split(" ");
		Request pedido = null;
		
		switch(splited[0]) {
		case "-m":
			pedido = new Request(from, RequestType.MESSAGE);
			pedido.setToContact(splited[1]);
			pedido.setMessage(splited[2]);
			break;
		case "-f":
			pedido = new Request(from, RequestType.FILE);
			pedido.setToContact(splited[1]);
				try {
					pedido.setFileByte(transformFiletoByte(splited[2]));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			break;
		case "-a":
			pedido = new Request(from, RequestType.ADDUSER);
			pedido.setToContact(splited[1]);
			pedido.setMessage(splited[2]);
			break;
		
		case "-d":
			pedido = new Request(from, RequestType.REMOVEUSER);
			pedido.setToContact(splited[1]);
			pedido.setMessage(splited[2]);
			break;
			
		case "-r":
			if(splited.length == 1) {
				pedido = new Request(from, RequestType.RECEIVER);
				pedido.setMessage("recent");
			}
			if(splited.length == 2) {
				pedido = new Request(from, RequestType.RECEIVER);
				pedido.setToContact(splited[1]);
				pedido.setMessage("all");
			}
			if(splited.length == 3) {
				pedido = new Request(from, RequestType.RECEIVER);
				pedido.setToContact(splited[1]);
				pedido.setMessage("file:" +splited[2]);
			}
			break;
			
		case "-u": 
			pedido = new Request(from, RequestType.AUTH);
			pedido.setMessage(splited[1]);
			break;
		}
		
		return pedido;
				
	}
	//tratar da exceçao
	private byte[] transformFiletoByte(String path) throws IOException {
		
		File file = new File(path);
		byte [] bFile = new byte[(int) file.length()]; //temos de limitar o tamanho do ficheiro
		
		FileInputStream fileInputStream = new FileInputStream(file);
	    int length = fileInputStream.read(bFile);
	    if(length != file.length())
	    	System.out.print("ERRO na transformação do ficheiro para byte");
	    	
	    fileInputStream.close();
	    
		return bFile;
	}

}
