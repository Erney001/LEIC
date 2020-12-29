package woo.core.exception;

/* Class to represent a non existing client key when trying to access a client */

public class MissingClientKeyException extends Exception {

	public MissingClientKeyException() {
  }

  /**
   * @param description
   */
  public MissingClientKeyException(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public MissingClientKeyException(Exception cause) {
    super(cause);
  }
}