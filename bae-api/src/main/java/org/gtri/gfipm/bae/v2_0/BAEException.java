package org.gtri.gfipm.bae.v2_0;

/**
 * Abstract highest-level exception to wrap all Exceptions that can occur when dealing with Backend Attribute Exchange
 * servers.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/05/02 3:25 PM
 */
public abstract class BAEException extends Exception {

    protected BAEException() {
    }

    protected BAEException(String message) {
        super(message);
    }

    protected BAEException(String message, Throwable cause) {
        super(message, cause);
    }

    protected BAEException(Throwable cause) {
        super(cause);
    }
}
