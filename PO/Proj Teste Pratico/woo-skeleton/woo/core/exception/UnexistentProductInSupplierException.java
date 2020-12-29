package woo.core.exception;

/* Class to represent a non existing product delivered by a supplier */

public class UnexistentProductInSupplierException extends Exception {

	public UnexistentProductInSupplierException() {
  }

  /**
   * @param description
   */
  public UnexistentProductInSupplierException(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public UnexistentProductInSupplierException(Exception cause) {
    super(cause);
  }
}