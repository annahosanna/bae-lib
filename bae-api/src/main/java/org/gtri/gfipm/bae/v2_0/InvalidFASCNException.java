package org.gtri.gfipm.bae.v2_0;

/**
 * Thrown when an invalid FACSN Number is encountered.
 * <br/><br/>
 * User: brad
 * Date: 4/6/13 10:21 AM
 */
public class InvalidFASCNException extends Exception {

    private String facsnNumber = null;

    public InvalidFASCNException(String facsnNumber, String reason){
        super(reason);
        this.facsnNumber = facsnNumber;
    }

}//end InvalidFASCNException()