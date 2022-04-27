package pt.tecnico.example.server.domain;

import pt.tecnico.example.server.domain.exception.InvalidNameException;

/** Domain root, where state and behavior of the server are implemented. */
public class Example {

	/** Maximum allowed lenght of the name. */
	private static int MAX_LEN_NAME = 8;

	/** Name to store. */
	private String name;

	/** Retrieve current name. */
	public synchronized String getName() {
		return name;
	}

	/** Store name. */
	public synchronized void setName(String name) throws InvalidNameException {
		if (name.isBlank()) {
			throw new InvalidNameException("The name should not be empty!");
		}
		if (name.length() > MAX_LEN_NAME) {
			throw new InvalidNameException("The name must have less than " + MAX_LEN_NAME + " characters!");
		}
		this.name = name;
	}

}
