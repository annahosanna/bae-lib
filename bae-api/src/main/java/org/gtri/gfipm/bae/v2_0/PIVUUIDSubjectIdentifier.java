package org.gtri.gfipm.bae.v2_0;

import gtri.logging.Logger;
import gtri.logging.LoggerFactory;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.GeneralName;

import java.io.IOException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

/**
 * Subject identifier implementation based on email address.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/05/01 9:00 PM
 */
public class PIVUUIDSubjectIdentifier implements SubjectIdentifier {
    //==================================================================================================================
    //  Static Constants
    //==================================================================================================================
    private static Logger logger = LoggerFactory.get(PIVUUIDSubjectIdentifier.class);
    public static final String FORMAT = "urn:idmanagement.gov:icam:bae:v2:SAML:2.0:nameid-format:uuid";
    //==================================================================================================================
    //  Public Static Methods
    //==================================================================================================================
    public static Boolean hasPIVIUUID( X509Certificate cert ) throws CertificateParsingException, IOException {
        Collection<List<?>> altNamesColection = cert.getSubjectAlternativeNames();
        if( altNamesColection != null && !altNamesColection.isEmpty() ){
            logger.debug("Found @|cyan "+altNamesColection.size()+"|@ subjectAltNames, which might be PIVI-UUID uri values...");
            for( List<?> subjectAltName : altNamesColection ){
                logger.info("Subject Alt Name Found: "+subjectAltName);
                if( ((Integer) subjectAltName.get(0)) == GeneralName.uniformResourceIdentifier ){
                    Object obj = subjectAltName.get(1);
                    if( obj != null && obj instanceof String ){
                        String uri = (String) obj;
                        if( uri.startsWith("urn:uuid:") ){
                            logger.info("Found PIVI UUID: @|cyan "+uri+"|@");
                            return true;
                        }else{
                            logger.warn("Found SubjectAltName uniformResourceIdentifier, but it's value does not start with 'urn:uuid:': "+uri);
                        }
                    }else{
                        logger.warn("Found SubjectAltName uniformResourceIdentifier, but it's value is not a valid string: "+obj);
                    }
                }
            }
        }
        return false;
    }//end hasFASCN()

    //==================================================================================================================
    //  Constructors
    //==================================================================================================================
    public PIVUUIDSubjectIdentifier(String uri){
        this.uriString = uri;
    }
    public PIVUUIDSubjectIdentifier(X509Certificate cert) throws CertificateParsingException {
        Collection<List<?>> altNamesColection = cert.getSubjectAlternativeNames();
        if( altNamesColection != null && !altNamesColection.isEmpty() ){
            logger.debug("Found @|cyan "+altNamesColection.size()+"|@ subjectAltNames, which might be PIVI-UUID uri values...");
            for( List<?> subjectAltName : altNamesColection ){
                logger.info("Subject Alt Name Found: "+subjectAltName);
                if( ((Integer) subjectAltName.get(0)) == GeneralName.uniformResourceIdentifier ){
                    Object obj = subjectAltName.get(1);
                    if( obj != null && obj instanceof String ){
                        String uri = (String) obj;
                        if( uri.startsWith("urn:uuid:") ){
                            logger.info("Found PIVI UUID: @|cyan "+uri+"|@");
                            this.uriString = uri;
                            break;
                        }
                    }
                }
            }
        }
        if( this.uriString == null )
            throw new CertificateParsingException("Could not locate valid PIVI UUID");
    }
    //==================================================================================================================
    //  Instance Variables
    //==================================================================================================================
    private String uriString;

    //==================================================================================================================
    //  Getters
    //==================================================================================================================
    public String getUriString(){
        return uriString;
    }
    //==================================================================================================================
    //  Setters
    //==================================================================================================================

    //==================================================================================================================
    //  Private/Protected Methods
    //==================================================================================================================

    //==================================================================================================================
    //  Public Methods
    //==================================================================================================================
    @Override
    public String getFormat() {
        return FORMAT;
    }

    @Override
    public String getName() {
        return this.getUriString();
    }


    @Override
    public int hashCode() {
        return this.getUriString().toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if( obj != null && obj instanceof SubjectIdentifier )
            return this.getName().equalsIgnoreCase(((SubjectIdentifier) obj).getName());
        return false;
    }

    @Override
    public String toString() {
        return "PIVUUIDSubjectIdentifier["+this.getUriString()+"]";
    }

}
