package exceptions;

public class InvalidMacException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 779290579511361745L;

	public InvalidMacException() {
	}

	public InvalidMacException(String message) {
		super(message);
	}
}
