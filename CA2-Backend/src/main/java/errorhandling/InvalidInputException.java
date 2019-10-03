package errorhandling;

import javax.ws.rs.WebApplicationException;

/**
 *
 * @author andreas
 */
public class InvalidInputException extends WebApplicationException {
    public InvalidInputException(String msg) {
        super(msg, 400);
    }
}
