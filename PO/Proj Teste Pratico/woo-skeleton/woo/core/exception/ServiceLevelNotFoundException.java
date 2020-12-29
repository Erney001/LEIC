package woo.core.exception;

/**
 * Class for representing an unexisting Service Level error when creating a product.
 */
public class ServiceLevelNotFoundException extends Exception {

  /**
   * Default constructor
   */
  public ServiceLevelNotFoundException() {
    // do nothing
  }

  /**
   * @param description
   */
  public ServiceLevelNotFoundException(String description) {
    super(description);
  }

  /**
   * @param cause
   */
  public ServiceLevelNotFoundException(Exception cause) {
    super(cause);
  }

}