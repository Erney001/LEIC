package woo.core.exception;

/* Class to represent exception when product as low stock to satisfy a sale */

public class LowStockException extends Exception {

	public LowStockException() {
    
  }

  /**
   * @param description
   */
  public LowStockException(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public LowStockException(Exception cause) {
    super(cause);
  }
}