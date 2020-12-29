package woo.core.exception;

/* Class to represent a duplicate supplier key when trying to register a supplier */

public class DupedSupplierKeyException extends Exception {

	public DupedSupplierKeyException() {
    
  }

  /**
   * @param description
   */
  public DupedSupplierKeyException(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public DupedSupplierKeyException(Exception cause) {
    super(cause);
  }
}