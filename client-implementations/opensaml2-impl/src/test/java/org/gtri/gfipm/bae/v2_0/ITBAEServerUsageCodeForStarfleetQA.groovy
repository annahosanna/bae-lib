package org.gtri.gfipm.bae.v2_0

import org.gtri.gfipm.bae.AbstractTest
import org.junit.Test
import org.opensaml.xml.security.SecurityHelper
import org.opensaml.xml.security.x509.X509Util

import java.security.PrivateKey
import java.security.cert.X509Certificate

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.notNullValue;

/**
 * Shows a usage example for the BAE Library.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/05/01
 * Time: 8:50 PM
 */
public class ITBAEServerUsageCodeForStarfleetQA extends AbstractTest {

    static String SERVER_ENDPOINT = "https://icam-gw.jhuapl.edu/BAE";
    static String DESTINATION_ID = "urn:dhs.gov:icam:bae:v1.0:test";

    static String SERVER_HTTPS_PUBLIC_CERTIFICATE_FILE_NAME = "xmlgw-dev.crt";
    static String SERVER_WS_SECURITY_PUBLIC_CERTIFICATE_FILE_NAME = "STARFLEET.crt";

    static String CLIENT_PRIVATE_KEY_FILE_NAME = "gtri-piv-2015.key"
    static String CLIENT_PRIVATE_KEY_PASSWORD = ""

    static String CLIENT_CERTIFICATE_FILE_NAME = "gtri-piv-2015.crt"

    static String ISSUER_ID = "URN:TEST:ICAM:BAE:V2:GTRI";
    static String ID_VALUE = "048051b4-2288-41fd-b895-5fe9945e1c63";


    @Test
    public void testQueryExample1() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {


        File privateKeyFile = getCertFile(CLIENT_PRIVATE_KEY_FILE_NAME)
        PrivateKey clientPrivateKey = SecurityHelper.decodePrivateKey(privateKeyFile, CLIENT_PRIVATE_KEY_PASSWORD.toCharArray())
        File clientCertFile = getCertFile(CLIENT_CERTIFICATE_FILE_NAME)
        Collection<X509Certificate> clientCerts = X509Util.decodeCertificate(clientCertFile)
        X509Certificate clientCert = (clientCerts && clientCerts.size() > 0) ? clientCerts.iterator().next() : null;
        if ( clientCert == null )
            throw new NullPointerException("Could not convert $clientCertFile to a certificate!")

        List<X509Certificate> serverCerts = []
        File serverCertFile = getCertFile(SERVER_HTTPS_PUBLIC_CERTIFICATE_FILE_NAME)
        serverCerts.addAll(X509Util.decodeCertificate(serverCertFile))
        File serverCertFile2 = getCertFile(SERVER_WS_SECURITY_PUBLIC_CERTIFICATE_FILE_NAME)
        serverCerts.addAll(X509Util.decodeCertificate(serverCertFile2))

        BAEServerInfo serverInfo = BAEServerInfoFactory.getInstance().createBAEServerInfo(SERVER_ENDPOINT, DESTINATION_ID, serverCerts);
        assertThat(serverInfo, notNullValue())
        BAEClientInfo clientInfo = BAEClientInfoFactory.getInstance().createBAEClientInfo(ISSUER_ID, clientCert, clientPrivateKey);
        assertThat(clientInfo, notNullValue())

        // BAEServer implementations are long lived and thread safe, so you should be able to cache it indefinitely.
        BAEServer baeServer = BAEServerFactory.getInstance().createBAEServer(serverInfo, clientInfo);
        assertThat(baeServer, notNullValue())

        SubjectIdentifier identifier = new PIVUUIDSubjectIdentifier(ID_VALUE);

        // This method could potentially take a while to return ( should be less than 10 seconds, though )
        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO do something with attributes...

    }//end queryExample1()


}//end queryExample1()