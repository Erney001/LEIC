package woo.core.exception;

/* Class to represent a duplicate client key when trying to register a client */

public class DupedClientKeyException extends Exception {

	public DupedClientKeyException() {
    
  }

  /**
   * @param description
   */
  public DupedClientKeyException(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public DupedClientKeyException(Exception cause) {
    super(cause);
  }
}