package domain;

import java.util.List;

import entities.ChatMessage;

public class UserInterface {

	/**
	 * imprime a mensagem  de como usar a aplicação
	 */
	public void printArgsUsage() {
		
		System.out.println("exemplo de uso:");
		System.out.println("myWhats <localUser> <serverAddress> [ -p <password> ] "
					+ "[ -m <contact> <message> | -f <contact> <file>  | -r contact file  |  "
					+ "-a <user> <group> |  -d <user> <group>  ]");
	}

	public void print(String string) {
		System.out.println(string);
		
	}
	
	public void printMessages(List<ChatMessage> list){
		
	}
	

}
