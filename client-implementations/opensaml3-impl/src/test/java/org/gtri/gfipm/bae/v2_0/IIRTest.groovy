package org.gtri.gfipm.bae.v2_0;

import org.gtri.gfipm.bae.AbstractTest
import org.junit.Ignore;
import org.junit.Test
//import org.opensaml.xml.security.SecurityHelper
//import org.opensaml.xml.security.x509.X509Util;
import org.opensaml.security.crypto.KeySupport
import org.opensaml.security.x509.X509Support

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;


import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

/**
 * Shows a usage example for the BAE Library.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/05/01
 * Time: 8:50 PM
 */
public class IIRTest extends AbstractTest {

    static String SERVER_ENDPOINT = "https://extsvc.iir.com/28cfr-bae/baeservice.svc";
    static String DESTINATION_ID = "URN:TEST:IDMANAGEMENT.GOV:ICAM:BAE:V2:BJA28CFR";

//    static String SERVER_PUBLIC_CERTIFICATE_FILE_NAME = "URN-TEST-IDMANAGEMENT.GOV-ICAM-BAE-V2-BJA28CFR.cer";
    static String SERVER_PUBLIC_CERTIFICATE_FILE_NAME = "iir-2015.crt";
    static String SERVER_PUBLIC_CERTIFICATE_FILE_NAME2 = "iir-2014.crt";

    static String CLIENT_PRIVATE_KEY_FILE_NAME = "gtri-pilot-iir.key";
    static String CLIENT_PRIVATE_KEY_PASSWORD = "";

    static String CLIENT_CERTIFICATE_FILE_NAME = "gtri-pilot-iir.crt";

    static String ISSUER_ID = "URN:TEST:IDMANAGEMENT.GOV:ICAM:BAE:V2:gtri-pilot-iir";

//    static String ID_VALUE = "Beverly.Crusher@test.dhs.gov";   // Direct Crusher FACSN
    static String ID_VALUE = "JMcAllister@iir.com";

    @Test
    public void testIIR() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {

        File privateKeyFile = getCertFile(CLIENT_PRIVATE_KEY_FILE_NAME)
        PrivateKey clientPrivateKey = KeySupport.decodePrivateKey(privateKeyFile, CLIENT_PRIVATE_KEY_PASSWORD.toCharArray())
        File clientCertFile = getCertFile(CLIENT_CERTIFICATE_FILE_NAME)
        //Collection<X509Certificate> clientCerts = X509Support.decodeCertificate(clientCertFile)
        //(clientCerts && clientCerts.size() > 0) ? clientCerts.iterator().next() : null;
        X509Certificate clientCert = X509Support.decodeCertificate(clientCertFile); 
        if ( clientCert == null )
            throw new NullPointerException("Could not convert $clientCertFile to a certificate!")

        List<X509Certificate> serverCerts = []
        File serverCertFile = getCertFile(SERVER_PUBLIC_CERTIFICATE_FILE_NAME)
        serverCerts.addAll(X509Support.decodeCertificate(serverCertFile))
        serverCertFile = getCertFile(SERVER_PUBLIC_CERTIFICATE_FILE_NAME2)
        serverCerts.addAll(X509Support.decodeCertificate(serverCertFile))

        BAEServerInfo serverInfo = BAEServerInfoFactory.getInstance().createBAEServerInfo(SERVER_ENDPOINT, DESTINATION_ID, serverCerts);
        assertThat(serverInfo, notNullValue())
        BAEClientInfo clientInfo = BAEClientInfoFactory.getInstance().createBAEClientInfo(ISSUER_ID, clientCert, clientPrivateKey);
        assertThat(clientInfo, notNullValue())
        def options = [:]
        options.put(WebServiceRequestOptions.CLIENT_CERT_AUTH, "false")
        options.put(WebServiceRequestOptions.SERVER_CERT_AUTH, "false")
        WebServiceRequestOptions wsRequestOptions = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(options);

        // BAEServer implementations are long lived and thread safe, so you should be able to cache it indefinitely.
        BAEServer baeServer = BAEServerFactory.getInstance().createBAEServer(serverInfo, clientInfo, wsRequestOptions);
        assertThat(baeServer, notNullValue())

        SubjectIdentifier identifier = new EmailSubjectIdentifier(ID_VALUE);

        // This method could potentially take a while to return ( should be less than 10 seconds, though )
        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);
        assertThat(attributes, notNullValue())
        assertThat(attributes.size(), equalTo(1))

        BackendAttribute attribute = attributes.iterator().next();
        assertThat(attribute, notNullValue())
        assertThat(attribute.getName(), equalTo("gfipm:2.0:user:28CFRCertificationIndicator"))
        assertThat(attribute.getValue().getBooleanValue(), equalTo(Boolean.FALSE))

        logger.info("Successfully tested IIR round-trip case")

    }//end testIIR()
}

