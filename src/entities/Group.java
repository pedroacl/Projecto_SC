package entities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Representação de um grupo
 * 
 * @author Pedro, Jose, Antonio
 *
 */
public class Group implements Serializable {
	
	private static final long serialVersionUID = 3719430979913465539L;

	private Long conversationId;
	
	private String name;
	
	private String admin;
	
	private Date createdAt;
	
	private List<String> users;
	
	
	public Group(String name, String admin, Long conversationId) {
		this.name = name;
		this.admin = admin;
		this.createdAt = new Date();
		this.conversationId = conversationId;
		
		users = new ArrayList<String>();
		users.add(admin);
	}
	
	/**
	 * Adiciona um novo utilizador a um grupo
	 * @param username - Nome do a ser adicionado ao grupo
	 * @return True, caso o utilizador seja adicionado
	 */
	public boolean addUser(String username) {
		if (!users.contains(username)) {
			users.add(username);
			return true;
		}
		
		return false;
	} 
	
	/**
	 * Devole o nome do grupo
	 * @return Nome do grupo
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Guarda o nome do grupo
	 * @param name, nome do grupo
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Define o dono do grupo, isto é, o utlizador que criou o grupo
	 * @return nome do user que criou o grupo
	 */
	public String getAdmin() {
		return admin;
	}
	
	/**
	 * Devolve a data de criação do grupo
	 * @return data de criaçao do grupo
	 */
	public Date getCreatedAt() {
		return createdAt;
	}
	
	/**
	 * Retorna os membros do grupo(inclui o dono do grupo)
	 * @return Lista com os nomes dos utilizadores que fazem parte do grupo
	 */
	public List<String> getUsers() {
		return users;
	}
	
	/**
	 * Devolve a identificador da conversa associada a este grupo
	 * @return id da conversa do grupo
	 */
	public Long getConversationId() {
		return conversationId;
	}
	
	/**
	 * Remove um membro deste grupo
	 * @param username nome do utilizador a remover
	 * @return true caso tenha sido removido
	 */
	public boolean removeMember(String username) {
		return users.remove(username);
	}
	
	public boolean contains(String username) {
		return users.contains(username);
	}
}
