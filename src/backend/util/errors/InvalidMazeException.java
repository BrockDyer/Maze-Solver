package backend.util.errors;

/**
 * An invalid maze exception.
 *
 * @author Brock Dyer.
 */
public class InvalidMazeException extends Throwable {

    /**
     * Create a new exception with the given message.
     * @param message the error message.
     */
    public InvalidMazeException(String message) {
        super(message);
    }
}
