package backend.util.errors;

/**
 * Thrown when a maze has no solution.
 *
 * @author Brock Dyer.
 */
public class NoSolutionException extends Throwable {

    public NoSolutionException(String message){
        super(message);
    }
}
