package woo.core.exception;

/* Class to represent a duplicate product key when trying to register a product */

public class DupedProductKeyException extends Exception {

	public DupedProductKeyException() {
    
  }

  /**
   * @param description
   */
  public DupedProductKeyException(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public DupedProductKeyException(Exception cause) {
    super(cause);
  }
}