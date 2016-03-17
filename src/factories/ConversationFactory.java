package factories;

import entities.Conversation;
import service.ConversationService;

/**
 * Classe singleton responsável por auxiliar a instanciação de conversations
 * associando-lhes um id que é incrementado à medida que são instanciados
 * objectos deste tipo.
 * 
 * @author José, Pedro, António
 *
 */
public class ConversationFactory {
	private static Long conversationId;

	private static ConversationFactory conversationFactory = new ConversationFactory();
	
	private ConversationFactory() {
		ConversationService conversationService = new ConversationService();
		conversationId = conversationService.getLastConversationId() + 1;
	}

	/**
	 * Permite obter a instancia do singleton
	 * 
	 * @return Devolve uma instancia com um id auto incrementado.
	 */
	public static ConversationFactory getInstance() {
		return conversationFactory;
	}

	/**
	 * Cria uma conversa com um id auto incrementado
	 * 
	 * @param fromUser,
	 *            utilizador que iniciou a conversa
	 * @param toUser,
	 *            contacto ao qual se destina a primeira mensagem
	 * @return Conversation
	 */
	public Conversation build(String fromUser, String toUser) {
		Conversation conversation = new Conversation(fromUser, toUser);
		conversation.setId(conversationId);
		conversationId++;

		return conversation;
	}

	/**
	 * Devolve o próximo id de uma conversation
	 * 
	 * @return Long, identificador de conversa
	 */
	public long generateID() {
		long id = conversationId;
		conversationId++;

		return id;
	}
}
