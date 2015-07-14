package org.gtri.gfipm.bae.v2_0;

/**
 * High-level class to describe any errors while constructing a {@link BAEServer}.
 * <br/><br/>
 * User: brad
 * Date: 4/6/13 8:59 AM
 */
public class BAEServerCreationException extends Exception {

    public BAEServerCreationException() {
    }

    public BAEServerCreationException(String message) {
        super(message);
    }

    public BAEServerCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BAEServerCreationException(Throwable cause) {
        super(cause);
    }
}
