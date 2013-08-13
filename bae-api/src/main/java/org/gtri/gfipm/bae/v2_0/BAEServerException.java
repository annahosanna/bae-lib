package org.gtri.gfipm.bae.v2_0;

/**
 * High-level class representing the multitude of problems which can occur on the client/server interaction.
 * <br/><br/>
 * User: brad
 * Date: 4/6/13 10:06 AM
 */
public class BAEServerException extends BAEException {
    public BAEServerException() {
    }

    public BAEServerException(String message) {
        super(message);
    }

    public BAEServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public BAEServerException(Throwable cause) {
        super(cause);
    }
}
