package org.gtri.gfipm.bae.v2_0;

/**
 * Subject identifier implementation based on email address.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/05/01 9:00 PM
 */
public class EmailSubjectIdentifier implements SubjectIdentifier {

    public static final String EMAIL_FORMAT = "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress";

    public EmailSubjectIdentifier(String emailAddress){
        this.emailAddress = emailAddress;
    }

    private String emailAddress;

    public String getEmailAddress() {
        return emailAddress;
    }


    @Override
    public String getFormat() {
        return EMAIL_FORMAT;
    }

    @Override
    public String getName() {
        return this.getEmailAddress();
    }


    @Override
    public int hashCode() {
        return this.getEmailAddress().toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if( obj != null && obj instanceof SubjectIdentifier )
            return this.getEmailAddress().equalsIgnoreCase(((SubjectIdentifier) obj).getName());
        return false;
    }

    @Override
    public String toString() {
        return "EmailSubjectIdentifier["+this.emailAddress+"]";
    }

}
