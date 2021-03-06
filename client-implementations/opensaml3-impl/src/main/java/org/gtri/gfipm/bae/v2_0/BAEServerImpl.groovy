package org.gtri.gfipm.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.apache.http.client.HttpClient
import org.apache.http.conn.HttpClientConnectionManager
import org.apache.http.impl.conn.BasicHttpClientConnectionManager
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
//import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.conn.ssl.SSLContexts
import org.apache.http.conn.ssl.SSLContextBuilder
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.gtri.gfipm.bae.util.AttributeQueryBuilder
import org.gtri.gfipm.bae.util.AttributeQuerySigner
import org.gtri.gfipm.bae.util.SoapEnvelopeBuilder
import org.gtri.gfipm.bae.util.WSS4jHttpSOAPClient
import org.opensaml.messaging.context.InOutOperationContext
import org.opensaml.messaging.context.MessageContext
import org.opensaml.core.xml.XMLObject
import org.opensaml.core.xml.io.Marshaller
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport
import org.opensaml.saml.saml2.core.AttributeQuery
import org.opensaml.saml.saml2.core.Response
import org.opensaml.saml.saml2.core.Assertion
import org.opensaml.saml.saml2.core.Attribute
import org.opensaml.saml.saml2.core.AttributeStatement
import org.opensaml.soap.client.SOAPClient
import org.opensaml.soap.client.SOAPClientContext
import org.opensaml.soap.messaging.context.SOAP11Context
import org.opensaml.soap.soap11.Envelope
import org.opensaml.xmlsec.signature.Signature
import org.opensaml.soap.client.http.HttpSOAPRequestParameters
import org.opensaml.saml.saml2.encryption.Decrypter
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver
import org.opensaml.security.credential.Credential
import org.opensaml.security.credential.CredentialSupport
import org.opensaml.xmlsec.encryption.support.DecryptionException
import java.security.KeyStore
import net.shibboleth.utilities.java.support.xml.SerializeSupport
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import org.w3c.dom.Element

import javax.net.ssl.SSLContext
import java.util.concurrent.TimeUnit

class BAEServerImpl implements BAEServer {
    //==================================================================================================================
    //  Static Constants/Variables
    //==================================================================================================================
    static Logger logger = LoggerFactory.get(BAEServerImpl)
    //==================================================================================================================
    //  Instance Variables
    //==================================================================================================================
    BAEClientInfo clientInfo
    BAEServerInfo serverInfo
    WebServiceRequestOptions webServiceRequestOptions

    //==================================================================================================================
    //  Public Interface Implementation Methods
    //==================================================================================================================
    @Override
    Collection<BackendAttribute> attributeQuery(SubjectIdentifier subjectId) throws BAEServerException {
        String txId = UUID.randomUUID().toString().toUpperCase();

        logger.info("[${txId}] Seting up parser pool...")
        BasicParserPool parserPool = new BasicParserPool();
        parserPool.setMaxPoolSize(10);
        parserPool.setNamespaceAware(true);
        parserPool.initialize();

        logger.info("[${txId}] Request to do attributeQuery on @|cyan ${subjectId}|@...")
        validateConfiguration();

        logger.debug("[${txId}] Building AttributeQuery Object...");
        AttributeQuery attributeQuery = AttributeQueryBuilder.build(subjectId, txId, this.getDestination(), this.getIssuerIdentifier());

        logger.debug("[${txId}] Sending AttributeQuery to be signed...");
        Signature attributeQuerySignature = AttributeQuerySigner.sign(attributeQuery, clientInfo);

        logger.debug("[${txId}] Building SOAP message contexts...");
        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(attributeQuery);
        SOAPClientContext scc = new SOAPClientContext();
        HttpSOAPRequestParameters soapAction = new HttpSOAPRequestParameters("AttributeQuery");
        scc.setSOAPRequestParameters(soapAction);
        messageContext.addSubcontext(scc);
        Envelope envelope = SoapEnvelopeBuilder.buildSoap11Envelope(messageContext);
        MessageContext inboundSoapContext = new MessageContext();
        inboundSoapContext.addSubcontext(new SOAP11Context(), true);
        InOutOperationContext inOutOperationContext = new InOutOperationContext(inboundSoapContext, messageContext);

        // TODO When submitting MANY requests per second, we should strive to use a pool of HTTP Clients, instead of building new ones.
        HttpClient httpClient = getHttpClient(txId);

        logger.debug("[${txId}] Sending Attribute Query to EndpointAddress[@|cyan ${this.serverInfo?.getEndpointAddress()}|@]...");
        SOAPClient soapClient = new WSS4jHttpSOAPClient(httpClient, parserPool, clientInfo.getPrivateKey(), clientInfo.getCertificate());
        soapClient.send(serverInfo.getEndpointAddress(), inOutOperationContext);

        logger.debug("Validating the response...");
        // TODO analyze the response
        MessageContext serverResponse = (MessageContext) inOutOperationContext.getInboundMessageContext();
        SOAP11Context  soapResponse   = (SOAP11Context)  serverResponse.getSubcontext(SOAP11Context);
        Response samlResponse = getResponse(soapResponse.getEnvelope())
        logger.info("Received response.  Status Code: "+samlResponse.getStatus().getStatusCode().getValue());

        logger.debug("Decrypting attributes...")
        Credential myCredential = CredentialSupport.getSimpleCredential (clientInfo.getCertificate(), clientInfo.getPrivateKey());
        Decrypter samlDecrypter = new Decrypter(null, new StaticKeyInfoCredentialResolver(myCredential), new InlineEncryptedKeyResolver());

        Assertion assertion = null;
        samlResponse.getEncryptedAssertions().each { encryptedAssertion ->
            if( assertion == null ){
                logger.debug("Decrypting assertion...")
                try {
                    assertion = samlDecrypter.decrypt(encryptedAssertion);
                    logger.info("Decrypted Assertion: \n${SerializeSupport.prettyPrintXML(assertion.getDOM())}")
                } catch (DecryptionException e) {
                    logger.error("Error decrypting saml!", e);
                    throw new BAEServerException("Error decrypting attributes.", e);
                }
            }
        }
        if( assertion == null ){
            logger.warn("Could not find any decrypted assertion!")
            throw new BAEServerException("Could not find any decrypted assertion in response from BAE server.");
        }

        List<BackendAttribute> backendAttributes = []
        logger.debug("Parsing attributes...")
        assertion.getAttributeStatements().each { AttributeStatement attrStmt ->
            attrStmt.getAttributes().each{ Attribute attribute ->
                BackendAttribute beAttr = convertAttribute( attribute );
                backendAttributes.add( beAttr )
                if( beAttr.values.size() == 1 )
                    logger.debug("  [@|cyan ${beAttr.name}|@] => [@|green ${beAttr.value}|@]")
                else{
                    StringBuffer buffer = new StringBuffer()
                    buffer.append("  [@|cyan ${beAttr.name}|@] => \n")
                    beAttr.values.each{ value ->
                        buffer.append("                   [@|green ${value}|@]\n")
                    }
                    logger.debug(buffer.toString())
                }
            }
        }

        logger.info("Query[@|green ${subjectId}|@] resulted in @|cyan ${backendAttributes.size()}|@ backend attributes.");
        return backendAttributes

    }//end attributeQuery()

    //==================================================================================================================
    //  Helper Methods
    //==================================================================================================================
    private BackendAttribute convertAttribute(Attribute attribute){
        BackendAttributeImpl beAttr = new BackendAttributeImpl()

        beAttr.name = attribute.getName()
        beAttr.friendlyName = attribute.friendlyName
        beAttr.nameFormat = AttributeNameFormat.fromUri(attribute.nameFormat)
        beAttr.values = convertToAttributeValues(attribute);

        return beAttr
    }//end convertAttribute()

    private List<BackendAttributeValue> convertToAttributeValues( XMLObject xmlObj ){
        Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(xmlObj);
        if( marshaller == null )
            throw new NullPointerException("Could not marshal SAML XMLObject.  No marshaller found for "+xmlObj);
        Element element = marshaller.marshall(xmlObj);
        String xml = SerializeSupport.prettyPrintXML(element);
        return BackendAttributeParser.parseValues(xml);
    }//end convertToString()


    /**
     * This method is called to check the configuration before performing any work.  If the configuration is found to
SLContextBuilder
     */
    private void validateConfiguration() throws BAEServerException {

        // TODO Check serverInfo & clientInfo
        // if ( clientInfo == 

    }//end validateConfiguration()

    private Boolean shouldUseClientCertInTLS() {
        if( webServiceRequestOptions )
            return webServiceRequestOptions.getBoolean(WebServiceRequestOptions.CLIENT_CERT_AUTH, WebServiceRequestOptions.CLIENT_CERT_AUTH_DEFAULT);
        else
            return WebServiceRequestOptions.CLIENT_CERT_AUTH_DEFAULT
    }

    private String getStringValue(String name, String defaultValue) {
        if( webServiceRequestOptions ){
            return webServiceRequestOptions.getString(name, defaultValue);
        }else{
            return defaultValue;
        }
    }

    private String getDestination() {
        return serverInfo.getDestination();
    }

    private String getIssuerIdentifier() {
        return clientInfo.getIdentifier();
    }

    private CloseableHttpClient getHttpClient(String txId) {
        logger.debug("[$txId] Building an HTTP Client...");
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
//        if ( webServiceRequestOptions )
//           httpClientBuilder.setConnectionTimeToLive(this.webServiceRequestOptions.getNumber(WebServiceRequestOptions.HTTP_CLIENT_TIMEOUT, WebServiceRequestOptions.HTTP_CLIENT_TIMEOUT_DEFAULT), TimeUnit.SECONDS)
//        else
//           httpClientBuilder.setConnectionTimeToLive(WebServiceRequestOptions.HTTP_CLIENT_TIMEOUT_DEFAULT, TimeUnit.SECONDS)


        //SSLContext sslContext = SSLContexts.createSystemDefault();
        //Generate a custom SSL Context based on server certificate provided during initialization
        KeyStore ks = buildKeyStore();
        KeyStore ts = buildTrustStore();
        SSLContextBuilder scb = new SSLContextBuilder();
        //scb.create();
        scb.loadTrustMaterial (ts, new TrustSelfSignedStrategy());

        if( shouldUseClientCertInTLS() ) {
            logger.info ("Will attempt client certificate authentication, adding private key to the TLS context.");
            scb.loadKeyMaterial   (ks, "".toCharArray());
        } else {
            logger.info ("Will not attempt client certificate authentication.");
        }

        SSLContext sslContext = scb.build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        httpClientBuilder.setSslcontext(sslContext)  // Redundant with following line?
        httpClientBuilder.setSSLSocketFactory(sslsf)
        //Only available in 4.5, setting it via the ConnectionSocketFactory above should work for 4.3
        //httpClientBuilder.setSSLHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
        httpClientBuilder.setConnectionManager(ccm);

        return httpClientBuilder.build();
    }

    private KeyStore buildKeyStore(){
        if( clientInfo == null )
            throw new NullPointerException("Cannot build tls key store without client info.") 
        if( clientInfo.getCertificate() == null )
            throw new BAEServerException ("No client certificate to authenticate to BAE Partner.") 
        if( clientInfo.getPrivateKey() == null )
            throw new BAEServerException ("No private key to authenticate to BAE Partner.") 

        // Create empty keystore
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null); // Creates an empty keystore

        // Add private key.
        char[] nopw = "".toCharArray();
        logger.info ("Adding Private Key with Cert (" + clientInfo.getCertificate().getSubjectX500Principal().getName() + ") to keystore.");
        ks.setKeyEntry("my client key", clientInfo.getPrivateKey(), nopw, clientInfo.getCertificate());

        return ks;
    }//end buildKeyStore()

    private KeyStore buildTrustStore(){
        if( serverInfo == null )
            throw new NullPointerException("Cannot build http trust store, since no server info.")
        if( serverInfo.getCertificates().isEmpty() )
            throw new BAEServerException ("No server certificate to validate BAE Server Partner.")

        // Create empty keystore
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null); // Creates an empty keystore

        // Add all certificates in the server trust chain
        for ( cert in serverInfo.getCertificates() ) {
           logger.debug ("Adding Cert (" + cert.getSubjectX500Principal().getName() + ") to keystore.");
           ks.setCertificateEntry(cert.getSubjectX500Principal().getName(), cert);
        }

        return ks;
    }//end buildTrustStore()

    private Response getResponse( Envelope soapResponse ){
        Response response = null;
        soapResponse.getBody().getUnknownXMLObjects().each{ xmlObject ->
            if( xmlObject instanceof Response )
                response = (Response) xmlObject;
        }
        return response
    }


}/* end BAEServerImpl */
