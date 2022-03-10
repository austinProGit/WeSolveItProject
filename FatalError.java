/**
 * A specific runtime exception that is thrown to indicate a fatal error specific to this program.
 * @author Thomas Merino
 * @version 1.0 (last modified 4/30/21)
 */
public class FatalError extends RuntimeException {

  /**
   * Construct the exception with a message.
   * @param message a brief description of the error.
   */
  public FatalError(String message) {
    super(message);
  }
}