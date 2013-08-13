package org.gtri.gfipm.bae.v2_0;

import org.junit.Test;

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
public class TestBAEServerUsageCode {


    @Test
    public void testNothing(){  }



    protected void queryExample1() throws BAEServerCreationException, BAEServerException, InvalidFASCNException {

        String endpointUrl = "https://somebaeserver.place/...";
        String destination = "urn:idmanagement.gov:...";
        List<X509Certificate> serverCertificates = null; // TODO Read from file.
        String clientIdentity = "urn:idmanagement.gov:...";
        X509Certificate clientCertificate = null; // TODO Read from file
        PrivateKey clientPrivateKey = null; // TODO Read from file


        BAEServerInfo serverInfo = BAEServerInfoFactory.getInstance().createBAEServerInfo(endpointUrl, destination, serverCertificates);
        BAEClientInfo clientInfo = BAEClientInfoFactory.getInstance().createBAEClientInfo(clientIdentity, clientCertificate, clientPrivateKey);

        // BAEServer implementations are long lived and thread safe, so you should be able to cache it indefinitely.
        BAEServer baeServer = BAEServerFactory.getInstance().createBAEServer(serverInfo, clientInfo);

        SubjectIdentifier identifier = new FASCNSubjectIdentifier("70001234000005119000000001170005");

        // This method could potentially take a while to return ( should be less than 10 seconds, though )
        Collection<BackendAttribute> attributes = baeServer.attributeQuery(identifier);

        // TODO do something with attributes...

    }//end queryExample1()








}//end queryExample1()