package woo.core.exception;

/**
 * Class for representing an unexisting Service Type error when creating a product.
 */
public class ServiceTypeNotFoundException extends Exception {

  /**
   * Default constructor
   */
  public ServiceTypeNotFoundException() {
    // do nothing
  }

  /**
   * @param description
   */
  public ServiceTypeNotFoundException(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public ServiceTypeNotFoundException(Exception cause) {
    super(cause);
  }

}