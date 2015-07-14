package org.gtri.gfipm.bae.v2_0;

import java.security.Principal;

/**
 * Represents a subject identifier to query on.  Note that the specification calls out a few, namely:
 * <ul>
 *     <li>Federal Agency Smart Credential Number (FACS-N)</li>
 *     <li>PIV-I UUID</li>
 *     <li>X.509 Subject DN</li>
 *     <li>E-mail Address</li>
 * </ul> <br/
 * Since some agencies are known to do other fields (ie, email address), however, instead of sticking to the spec
 * examples this is a more generic class.  The only required methods to implement are: <i>getFormat()</i> and <i>getValue()</i>.
 * <br/><br/>
 * User: brad
 * Date: 4/6/13 10:06 AM
 */
public interface SubjectIdentifier extends Principal {

    /**
     * The format of the identifier.  Note that this value is fixed for:
     * <ul>
     *  <li>FACS-N: <b>urn:idmanagement.gov:icam:bae:v2:SAML:2.0:nameid-format:fasc-n</b></li>
     *  <li>PIV-I UUID: <b>urn:idmanagement.gov:icam:bae:v2:SAML:2.0:nameid-format:uuid</b></li>
     *  <li>X.509 Subject DN: <b>urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName</b></li>
     *  <li>E-mail Address: urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress</li>
     * </ul>
     * <br/><br/>
     * @return a non-null, non-empty format {@link String}.
     */
    public String getFormat();

}//end SubjectIdentifier