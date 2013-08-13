package org.gtri.gfipm.bae.v2_0;

import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Information representing the details of the BAE Server.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/05/01 10:12 AM
 */
public interface BAEServerInfo {


    /**
     * Actual HTTP server address & full path for the BAE server.
     * <br/><br/>
     * @return the {@link String} representation of the BAE server's URL, with full path.
     */
    public String getEndpointAddress();

    /**
     * Returns the String representing the Destination attribute in the AttributeRequest XML format
     * <br/><br/>
     * @return a {@link String} representing the Destination attribute in the AttributeRequest XML format.
     */
    public String getDestination();

    /**
     * The {@link X509Certificate}s for the BAE server.  Used not only to verify the https server (if used in getEndpointAddress)
     * but also to check the signatures in the ws-security headers.
     * <br/><br/>
     * @return the server's {@link X509Certificate}s
     */
    public List<X509Certificate> getCertificates();


}//end BAEServerInfo()