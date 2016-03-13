package entities;

/**
 * Classe que representa um mensagem de Chat, isto é uma mensagem
 * entre utilizadores
 */

import java.io.Serializable;
import java.util.Date;

import network.MessageType;



public class ChatMessage implements Serializable {
	

	private static final long serialVersionUID = 8053113085617628635L;

	private String content;
	
	private String fromUser;
	
	private String destination;
	
	private MessageType type;
	
	private Date createdAt;
	
	/**
	 * Define a data da chatMessage
	 * 
	 * @param createdAt - Date, que representa a data de criaçao da mensagem
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	public ChatMessage( String from, String destination, String message, MessageType type ) {
		this.fromUser = from;
		this.destination = destination;
		this.type = type;
		this.content = message;
		createdAt = new Date();
	}
	
	/**
	 * Devolve a data de criaçao da mensagem
	 * @return Data, data de criaçao
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * Devolve o emissor da mensagem
	 * @return String com o nome do utilizador que enviou a mensagem
	 */
	public String getFromUser() {
		return fromUser;
	}
	
	/**
	 * Devove o destinatário da mensagem
	 * @return String com o nome de utilizador que recebeu a mensagem
	 */
	public String getDestination() {
		return destination;
	}
	
	/**
	 * Devolve o conteudo da mensagem
	 * @return String com o conteudo da mensagem
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * informa qual o tipo da mensagem
	 * @return MessageType que representa o tipo de mensagem
	 */
	public MessageType getMessageType() {
		return type;
	}
	
	@Override
	/**
	 * Verifica se outra chatMessage é igual a esta
	 * @param obj- Objecto a camparar 
	 * @return True se for igual, false caso contrario
	 */
	public boolean equals(Object obj) {
		ChatMessage chatMessage = (ChatMessage) obj;

		return chatMessage.content.equals(this.content) &&
				chatMessage.fromUser.equals(this.fromUser) &&
				chatMessage.destination.equals(this.destination) &&
				chatMessage.type.equals(this.type) &&
				chatMessage.createdAt.equals(this.createdAt);
	}

	/**
	 * Representação textual de uma ChatMensagem
	 * @return String com informaçao sobre a mensagem chat
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("From: " + fromUser + "\n");
		sb.append("To: " + destination + "\n");
		sb.append("Content: " + content + "\n");
		sb.append("Data: " + createdAt + "\n");
		
		return sb.toString();
	}
}
