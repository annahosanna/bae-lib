package org.gtri.gfipm.bae.v2_0

import java.security.PrivateKey
import java.security.cert.X509Certificate

/**
 * The client's detailed information in a BAE Server Attribute Exchange.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/05/01 10:22 AM
 */
interface BAEClientInfo {

    /**
     * The saml2:Issuer identifier for the AttributeQuery.  Must be understood by the BAE server.
     * <br/><br/>
     * @return a {@link String} representing the URN for the issuer identifier
     */
    public String getIdentifier();

    public X509Certificate getCertificate();

    public PrivateKey getPrivateKey();


}//end BAEClientInfo