package org.gtri.gfipm.bae.v2_0;

/**
 * Thrown whenever a value is coerced into a type it doesn't understand.  For example, a boolean string "true" being
 * coerced into XML.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/05/02 3:24 PM
 */
public class ValueCoerceException extends BAEException {


    public ValueCoerceException() {
    }

    public ValueCoerceException(String message) {
        super(message);
    }

    public ValueCoerceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValueCoerceException(Throwable cause) {
        super(cause);
    }


    private String value;
    private Class coersionType;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Class getCoersionType() {
        return coersionType;
    }

    public void setCoersionType(Class coersionType) {
        this.coersionType = coersionType;
    }
}
