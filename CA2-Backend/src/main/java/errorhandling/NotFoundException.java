package errorhandling;

import javax.ws.rs.WebApplicationException;

/**
 *
 * @author andreas
 */
public class NotFoundException extends WebApplicationException {
    public NotFoundException(String msg) {
        super(msg, 404);
    }
}
