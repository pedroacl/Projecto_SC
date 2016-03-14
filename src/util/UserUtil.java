package util;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

import entities.ChatMessage;

public class UserUtil {

	/**
	 * imprime a mensagem de como usar a aplicação
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

	public void printContactChatMessages(List<ChatMessage> list, String username) {
		for (ChatMessage cm : list)
			printContactChatMessage(cm, username, true);
	}

	public void printChatMessages(List<ChatMessage> list, String username) {
		for (ChatMessage cm : list)
			printContactChatMessage(cm, username, false);
	}

	private void printContactChatMessage(ChatMessage chatMessage, String username, boolean isContact) {
		StringBuilder sb = new StringBuilder();

		if (isContact)
			sb.append("Contact: " + chatMessage.getDestination() + "\n");
		
		if (username.equals(chatMessage.getFromUser())) {
			sb.append("me: ");
		} else {
			sb.append(chatMessage.getFromUser() + ": ");
		}

		sb.append(chatMessage.getContent() + "\n");
		
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm");  
		String date = dt.format(chatMessage.getCreatedAt());

		sb.append(date);

		System.out.println(sb);
	}
	
	public String askForPassword () {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Qual a sua password?");
		String lido = sc.nextLine();
		
		return lido;
	}

}
