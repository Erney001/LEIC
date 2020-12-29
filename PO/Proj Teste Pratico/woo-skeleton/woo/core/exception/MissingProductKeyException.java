package woo.core.exception;

/* Class to represent a non existing product key when trying to access a product */

public class MissingProductKeyException extends Exception {

	public MissingProductKeyException() {
  }

  /**
   * @param description
   */
  public MissingProductKeyException(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public MissingProductKeyException(Exception cause) {
    super(cause);
  }
}