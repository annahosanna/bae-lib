package org.gtri.gfipm.bae.v2_0

import org.gtri.gfipm.bae.AbstractTest
import org.junit.Before
import org.junit.BeforeClass
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
public class ITFindGFIPMAttributesForStarfleetQA extends AbstractTest {

    static String SERVER_ENDPOINT = "https://icam-gw.jhuapl.edu/BAE";
    static String DESTINATION_ID = "urn:dhs.gov:icam:bae:v1.0:test";

    static String SERVER_HTTPS_PUBLIC_CERTIFICATE_FILE_NAME = "xmlgw-dev.crt";
    static String SERVER_WS_SECURITY_PUBLIC_CERTIFICATE_FILE_NAME = "STARFLEET.crt";

    static String CLIENT_PRIVATE_KEY_FILE_NAME = "gtri-piv-2015.key"
    static String CLIENT_PRIVATE_KEY_PASSWORD = ""

    static String CLIENT_CERTIFICATE_FILE_NAME = "gtri-piv-2015.crt"

    static String ISSUER_ID = "URN:TEST:ICAM:BAE:V2:GTRI";

//    static String PIVI_UUID = "urn:uuid:048051b4-2288-41fd-b895-5fe9945e1c63";
    static String PIVI_UUID = "048051b4-2288-41fd-b895-5fe9945e1c63";




    static BAEServer baeServer = null

    @Before
    public void setUpBaeServer() {
        logger.info("Initializing BAE Server...")
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
        baeServer = BAEServerFactory.getInstance().createBAEServer(serverInfo, clientInfo);
        assertThat(baeServer, notNullValue())
    }//end setup


    @Test
    public void testQueryUUID() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {
        // Query on the PIV-I UUID
        SubjectIdentifier id = new PIVUUIDSubjectIdentifier (PIVI_UUID);

        def opts = [:]

        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML, "true")
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, "./target/piv-uuid-test.xml")
        WebServiceRequestOptions options = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(opts);
        assertThat(options, notNullValue())
        baeServer.setWebServiceRequestOptions(options);

        Collection<BackendAttribute> attributes = baeServer.attributeQuery(id);

        // TODO Validate Attributes Somehow...

    }//end queryExample1()


    @Test
    public void testGfipmAttributes1() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {
        // TODO do something with attributes...
        String fascn = "32010295759494116464979587132011";
        SubjectIdentifier identifier = new FASCNSubjectIdentifier(fascn);

        def opts = [:]
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML, "true")
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, "./target/"+fascn+".xml")
        WebServiceRequestOptions options = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(opts);
        assertThat(options, notNullValue())
        baeServer.setWebServiceRequestOptions(options);

        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO Validate Attributes Somehow...

    }//end queryExample1()




    @Test
    public void testGfipmAttributes2() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {
        // TODO do something with attributes...
        String fascn = "32010295723474119614127727132015";
        SubjectIdentifier identifier = new FASCNSubjectIdentifier(fascn);

        def opts = [:]
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML, "true")
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, "./target/"+fascn+".xml")
        WebServiceRequestOptions options = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(opts);
        assertThat(options, notNullValue())
        baeServer.setWebServiceRequestOptions(options);

        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO Validate Attributes Somehow...

    }//end queryExample2()


    @Test
    public void testGfipmAttributes3() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {
        // TODO do something with attributes...
        String fascn = "32018575714931127163720148132011";
        SubjectIdentifier identifier = new FASCNSubjectIdentifier(fascn);

        def opts = [:]
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML, "true")
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, "./target/"+fascn+".xml")
        WebServiceRequestOptions options = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(opts);
        assertThat(options, notNullValue())
        baeServer.setWebServiceRequestOptions(options);

        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO Validate Attributes Somehow...

    }//end queryExample3()



    @Test
    public void testGfipmAttributes4() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {
        // TODO do something with attributes...
        String fascn = "32011922843789234110207347132011";
        SubjectIdentifier identifier = new FASCNSubjectIdentifier(fascn);

        def opts = [:]
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML, "true")
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, "./target/"+fascn+".xml")
        WebServiceRequestOptions options = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(opts);
        assertThat(options, notNullValue())
        baeServer.setWebServiceRequestOptions(options);

        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO Validate Attributes Somehow...

    }//end queryExample4()


    @Test
    public void testGfipmAttributes5() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {
        // TODO do something with attributes...
        String fascn = "32013733334893131152472674132011";
        SubjectIdentifier identifier = new FASCNSubjectIdentifier(fascn);

        def opts = [:]
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML, "true")
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, "./target/"+fascn+".xml")
        WebServiceRequestOptions options = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(opts);
        assertThat(options, notNullValue())
        baeServer.setWebServiceRequestOptions(options);

        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO Validate Attributes Somehow...

    }//end queryExample5()



    @Test
    public void testGfipmAttributes6() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {
        // TODO do something with attributes...
        String fascn = "32010889895303114340730641132015";
        SubjectIdentifier identifier = new FASCNSubjectIdentifier(fascn);

        def opts = [:]
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML, "true")
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, "./target/"+fascn+".xml")
        WebServiceRequestOptions options = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(opts);
        assertThat(options, notNullValue())
        baeServer.setWebServiceRequestOptions(options);

        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO Validate Attributes Somehow...

    }//end queryExample6()



    @Test
    public void testGfipmAttributes7() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {
        // TODO do something with attributes...
        String fascn = "32015167114200114001354205132011";
        SubjectIdentifier identifier = new FASCNSubjectIdentifier(fascn);

        def opts = [:]
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML, "true")
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, "./target/"+fascn+".xml")
        WebServiceRequestOptions options = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(opts);
        assertThat(options, notNullValue())
        baeServer.setWebServiceRequestOptions(options);

        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO Validate Attributes Somehow...

    }//end queryExample7()



    @Test
    public void testGfipmAttributes8() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {
        // TODO do something with attributes...
        String fascn = "32015424119949112243747282132015";
        SubjectIdentifier identifier = new FASCNSubjectIdentifier(fascn);

        def opts = [:]
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML, "true")
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, "./target/"+fascn+".xml")
        WebServiceRequestOptions options = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(opts);
        assertThat(options, notNullValue())
        baeServer.setWebServiceRequestOptions(options);

        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO Validate Attributes Somehow...

    }//end queryExample8()



    @Test
    public void testGfipmAttributes9() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {
        // TODO do something with attributes...
        String fascn = "32011624556438139545326551132011";
        SubjectIdentifier identifier = new FASCNSubjectIdentifier(fascn);

        def opts = [:]
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML, "true")
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, "./target/"+fascn+".xml")
        WebServiceRequestOptions options = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(opts);
        assertThat(options, notNullValue())
        baeServer.setWebServiceRequestOptions(options);

        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO Validate Attributes Somehow...

    }//end queryExample9()



    @Test
    public void testGfipmAttributes10() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {
        // TODO do something with attributes...
        String fascn = "32012096177465118949244215132011";
        SubjectIdentifier identifier = new FASCNSubjectIdentifier(fascn);

        def opts = [:]
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML, "true")
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, "./target/"+fascn+".xml")
        WebServiceRequestOptions options = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(opts);
        assertThat(options, notNullValue())
        baeServer.setWebServiceRequestOptions(options);

        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO Validate Attributes Somehow...

    }//end queryExample10()



    @Test
    public void testGfipmAttributes11() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {
        // TODO do something with attributes...
        String fascn = "99999999999999116998931393132016";
        SubjectIdentifier identifier = new FASCNSubjectIdentifier(fascn);

        def opts = [:]
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML, "true")
        opts.put(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, "./target/"+fascn+".xml")
        WebServiceRequestOptions options = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(opts);
        assertThat(options, notNullValue())
        baeServer.setWebServiceRequestOptions(options);

        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO Validate Attributes Somehow...

    }//end queryExample11()







}//end queryExample1()
