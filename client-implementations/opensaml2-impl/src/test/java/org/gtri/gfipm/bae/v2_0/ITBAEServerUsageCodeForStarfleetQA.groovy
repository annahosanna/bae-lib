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

    static String SERVER_ENDPOINT = "https://aplgig-xml.jhuapl.edu/ICAM/BAE/ExternalBAEService/v2.0/QA";
    static String DESTINATION_ID = "urn:test:idmanagement.gov:icam:bae:v2:starfleet";

    static String SERVER_HTTPS_PUBLIC_CERTIFICATE_FILE_NAME = "cert_starfleet.der";
    static String SERVER_WS_SECURITY_PUBLIC_CERTIFICATE_FILE_NAME = "SSL_Entrust_ChainRoot_CA.der";

    static String CLIENT_PRIVATE_KEY_FILE_NAME = "GTRI_PILOT.p8.pem"
    static String CLIENT_PRIVATE_KEY_PASSWORD = "jhuapl"

    static String CLIENT_CERTIFICATE_FILE_NAME = "cert_GTRI_PILOT.pem"

    static String ISSUER_ID = "urn:test:idmanagement.gov:icam:bae:v2:GTRI_PILOT";
    static String ID_VALUE = "70001234000005119000000005170225";   // Direct Crusher FACSN


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

        SubjectIdentifier identifier = new FASCNSubjectIdentifier(ID_VALUE);

        // This method could potentially take a while to return ( should be less than 10 seconds, though )
        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO do something with attributes...

    }//end queryExample1()


}//end queryExample1()