package org.gtri.gfipm.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.gtri.gfipm.bae.AbstractTest
import org.junit.Test
import org.gtri.gfipm.bae.v2_0.BAEServerImpl
import org.gtri.gfipm.bae.v2_0.PIVUUIDSubjectIdentifier

import org.opensaml.security.crypto.KeySupport
import org.opensaml.security.x509.X509Support 
import java.security.PrivateKey
import java.security.cert.X509Certificate

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.notNullValue



/**
 * Created by brad on 7/16/15.
 */
class BAEServerImplTest extends AbstractTest {

    static Logger logger = LoggerFactory.get(BAEServerImplTest.class);

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
    public void testIt(){
        logger.info("Testing Ability to Query JHUAPL BAE Server...")

        File privateKeyFile = getCertFile(CLIENT_PRIVATE_KEY_FILE_NAME)
        PrivateKey clientPrivateKey = KeySupport.decodePrivateKey(privateKeyFile, CLIENT_PRIVATE_KEY_PASSWORD.toCharArray())
        File clientCertFile = getCertFile(CLIENT_CERTIFICATE_FILE_NAME)
        Collection<X509Certificate> clientCerts = X509Support.decodeCertificates(clientCertFile)
        X509Certificate clientCert = (clientCerts && clientCerts.size() > 0) ? clientCerts.iterator().next() : null;
        //X509Certificate clientCert = X509Support.decodeCertificate(clientCertFile) 

        if ( clientPrivateKey == null )
            throw new NullPointerException("Could not convert $privateKeyFile to a key!")

        if ( clientCert == null )
            throw new NullPointerException("Could not convert $clientCertFile to a certificate!")

        List<X509Certificate> serverCerts = []
        File serverCertFile = getCertFile(SERVER_HTTPS_PUBLIC_CERTIFICATE_FILE_NAME)
        serverCerts.addAll(X509Support.decodeCertificate(serverCertFile))
        File serverCertFile2 = getCertFile(SERVER_WS_SECURITY_PUBLIC_CERTIFICATE_FILE_NAME)
        serverCerts.addAll(X509Support.decodeCertificate(serverCertFile2))

        BAEServerInfo serverInfo = BAEServerInfoFactory.getInstance().createBAEServerInfo(SERVER_ENDPOINT, DESTINATION_ID, serverCerts);
        assertThat(serverInfo, notNullValue())
        BAEClientInfo clientInfo = BAEClientInfoFactory.getInstance().createBAEClientInfo(ISSUER_ID, clientCert, clientPrivateKey);
        assertThat(clientInfo, notNullValue())

        // BAEServer implementations are long lived and thread safe, so you should be able to cache it indefinitely.
        BAEServer baeServer = BAEServerFactory.getInstance().createBAEServer(serverInfo, clientInfo);
        assertThat(baeServer, notNullValue())

        SubjectIdentifier identifier = new PIVUUIDSubjectIdentifier(ID_VALUE);

        // This method could potentially take a while to return ( should be less than 10 seconds, though )
       //  Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);
        

//        BAEServerImpl baeServer = new BAEServerImpl();
//        baeServer.attributeQuery(new PIVUUIDSubjectIdentifier(ID_VALUE));
       

    }//end testIt()

}
