package woo.core.exception;

/* Class to represent a non existing product delivered by a supplier */

public class UnexistentProductInSupplier extends Exception {

	public UnexistentProductInSupplier() {
  }

  /**
   * @param description
   */
  public UnexistentProductInSupplier(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public UnexistentProductInSupplier(Exception cause) {
    super(cause);
  }
}