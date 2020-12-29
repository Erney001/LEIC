package woo.core.exception;

/* Class to represent a non existing supplier key when trying to access a supplier */

public class MissingSupplierKeyException extends Exception {

	public MissingSupplierKeyException() {
  }

  /**
   * @param description
   */
  public MissingSupplierKeyException(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public MissingSupplierKeyException(Exception cause) {
    super(cause);
  }
}