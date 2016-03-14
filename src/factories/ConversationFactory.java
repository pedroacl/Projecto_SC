package factories;

import entities.Conversation;

/**
 * Classe responsavel por criar conversaçoes associando-lhe um id.
 * 
 * @author Jose, Pedro, Antonio
 *
 */
public class ConversationFactory {
	
	private static Long conversationId;
	
	private static ConversationFactory conversationFactory = new ConversationFactory();
	
	
	private ConversationFactory() {
		reset();
	}
	

	public void reset() {
		conversationId = 1L;
	}
	
	
	public static ConversationFactory getInstance() {
		return conversationFactory;
	}
	
	/**
	 * Cria uma conversa
	 * @param fromUser, utilizador que iniciou a conversa
	 * @param toUser, contacto a que mse destina a primeira mensagem
	 * @return Conversation
	 */
	public Conversation build(String fromUser, String toUser) {
		Conversation conversation = new Conversation(fromUser, toUser);
		conversation.setId(conversationId);
		System.out.println("[CONVERSATIOFACTORY)]: "+ conversationId);
		conversationId++;
		
		return conversation;
	}
	
	/**
	 * Devolve o próximo id de uma conversa
	 * @return Long, identificador de conversa
	 */
	public long generateID() {
		System.out.println("[CONVERSATIOFACTORY)]: "+ conversationId++);
		return conversationId++;
		
	}
}
