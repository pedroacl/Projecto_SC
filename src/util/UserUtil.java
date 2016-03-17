package util;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

import entities.ChatMessage;

/**
 * Classe que trata da interacção com o utilizador 
 * @author António, José e Pedro
 *
 */
public class UserUtil {

	/**
	 * Imprime a mensagem de como usar a aplicação
	 */
	public void printArgsUsage() {
		System.out.println("Como usar:");
		System.out.println("myWhats <localUser> <serverAddress> [ -p <password> ] "
				+ "[ -m <contact> <message> | -f <contact> <file>  | -r contact file  |  "
				+ "-a <user> <group> |  -d <user> <group>  ]");
	}

	/**
	 * Imprime uma mensagem de acordo com o seu conteúdo
	 * @param string Conteúdo da mensagem
	 */
	public void print(String string) {
		if (string.equals("OK")) {
			System.out.println("Operacao realizada com sucesso: " + string);
		} else {
			System.err.println(string);
		}
	}

	/**
	 * Mostra o conteúdo das diversas mensagens trocadas com o utilizador
	 * @param chatMessages Lista de mensagens trocadas com o utilizador
	 * @param username Nome do utilizador que participou nas mensagens
	 */
	public void printContactChatMessages(List<ChatMessage> chatMessages, String username) {
		for (ChatMessage cm : chatMessages)
			printContactChatMessage(cm, username, true);
	}

	/**
	 * Mostra o conteúdo das diversas mensagens de uma conversa
	 * @param chatMessages Lista de mensagens a serem mostradas
	 * @param username Nome do utilizador que participou nas mensagens
	 */
	public void printChatMessages(List<ChatMessage> chatMessages, String username) {
		for (ChatMessage cm : chatMessages)
			printContactChatMessage(cm, username, false);
	}

	/**
	 * Mostra o conteúdo de uma mensagem
	 * @param chatMessage Mensagem a ser impresso o respectivo conteúdo
	 * @param username Nome do utilizador quem enviou a mensagem
	 * @param isContact Utilizador é ou não um contacto
	 */
	private void printContactChatMessage(ChatMessage chatMessage, String username, boolean isContact) {
		StringBuilder sb = new StringBuilder();

		if (isContact)
			sb.append("Contact: " + chatMessage.getDestination() + "\n");
		
		// mensagem enviada pelo proprio utilizador
		if (username.equals(chatMessage.getFromUser())) {
			sb.append("me: ");
		} else {
			sb.append(chatMessage.getFromUser() + ": ");
		}

		sb.append(chatMessage.getContent() + "\n");
		
		// formatar data para apresentacao
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm");  
		String date = dt.format(chatMessage.getCreatedAt());

		sb.append(date);

		System.out.println(sb);
	}
	
	/**
	 * Pede a password ao utilizador
	 * @return Devolve a password introduzida pelo utilizador
	 */
	public String askForPassword () {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Qual a sua password?");
		String lido = sc.nextLine();
		sc.close();

		return lido;
	}

}
