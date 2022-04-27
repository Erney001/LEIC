package pt.tecnico.example.server.domain.exception;

/** Thrown when the provided name is not valid. */
public class InvalidNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidNameException(String message) {
		super(message);
	}

}
