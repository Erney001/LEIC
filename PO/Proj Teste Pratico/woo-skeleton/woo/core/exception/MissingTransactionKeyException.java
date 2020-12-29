package woo.core.exception;

/* Class to represent a non existing transaction key when trying to access a transaction */

public class MissingTransactionKeyException extends Exception {

	public MissingTransactionKeyException() {
  }

  /**
   * @param description
   */
  public MissingTransactionKeyException(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public MissingTransactionKeyException(Exception cause) {
    super(cause);
  }
}