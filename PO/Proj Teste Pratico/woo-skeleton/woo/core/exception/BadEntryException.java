package woo.core.exception;

/**
 * Exception for unknown import file entries.
 */
public class BadEntryException extends Exception {

  /** Class serial number. */
  private static final long serialVersionUID = 201409301048L;

  /**
   * @param entrySpecification
   */
  public BadEntryException(String entrySpecification) {
    super(entrySpecification);
  }

  /**
   * @param entrySpecification
   * @param cause
   */
  public BadEntryException(String entrySpecification, Exception cause) {
    super(entrySpecification, cause);
  }
}
