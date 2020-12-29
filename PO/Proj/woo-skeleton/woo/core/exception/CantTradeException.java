package woo.core.exception;

/* Class to represent a supplier with off status - cant trade */

public class CantTradeException extends Exception {

	public CantTradeException() {
  }

  /**
   * @param description
   */
  public CantTradeException(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public CantTradeException(Exception cause) {
    super(cause);
  }
}