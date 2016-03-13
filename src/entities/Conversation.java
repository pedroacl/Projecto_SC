package entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Informação sobre uma conversa entre 2 utilizadores
 * 
 * @author Pedro,Jose, António
 *
 */
public class Conversation implements Serializable {

	private static final long serialVersionUID = 6123506731411848219L;

	private Long id;

	private String fromUser;

	private String toUser;

	private Date lastMessageDate;


	public Conversation(String fromUser, String toUser) {
		this.fromUser = fromUser;
		this.toUser = toUser;
	}
	
	/**
	 * Identifica a ultima mensagem da conversa
	 * @return Date, data da ultima mensagem da conversa
	 */
	public Date getLastMessageDate() {
		return lastMessageDate;
	}

	/**
	 * Define a data da ultima mensagem entre os interlocutores
	 * @param lastMessageDate- data da ultima mensagem da conversa
	 */
	public void setLastMessageDate(Date lastMessageDate) {
		this.lastMessageDate = lastMessageDate;
	}
	
	/**
	 * Retorna o identificador da conversa
	 * @return Long com o id da conversa
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * Define o identificador da conversa
	 * @param id- id da conversa
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Devolve o utilizador que iniciou a conversa
	 * @return Nome do utilizador que iniciou a conversa
	 */
	public String getFromUser() {
		return fromUser;
	}
	
	/**
	 * Define quem iniciou a conversa
	 * @param fromUser, nome do utilizador que iniciou a conversa
	 */
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	
	/**
	 * Contacto que participa na conversa
	 * @return nome do contacto(user ou grupo) que está a participar na conversa
	 */
	public String getToUser() {
		return toUser;
	}
	
	/**
	 * Define o contacto a a primeira mensagem se destina
	 * @param toUser- contacto(group ou user) que participa na conversa
	 */
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	@Override
	public boolean equals(Object obj) {
		Conversation conversation = (Conversation) obj;
		return (this.id == conversation.getId());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Id: " + id + "\n");
		sb.append("From: " + fromUser + "\n");
		sb.append("To: " + toUser + "\n");

		return sb.toString();
	}
}
