package org.gtri.gfipm.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.apache.http.client.HttpClient
import org.apache.http.conn.HttpClientConnectionManager
import org.apache.http.impl.conn.BasicHttpClientConnectionManager
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.apache.http.ssl.SSLContextBuilder
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
import org.opensaml.saml.saml2.core.AttributeQuery
import org.opensaml.soap.client.SOAPClient
import org.opensaml.soap.client.SOAPClientContext
import org.opensaml.soap.messaging.context.SOAP11Context
import org.opensaml.soap.soap11.Envelope
import org.opensaml.xmlsec.signature.Signature
import org.opensaml.soap.client.http.HttpSOAPRequestParameters
import java.security.KeyStore
import net.shibboleth.utilities.java.support.xml.SerializeSupport
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;


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

        // How many subcontexts does messageContext have?
        // TODO - Write a sanity check to look for subContexts and make sure there is an SOAPClientContext and SOAP11Context...
        

        // TODO When submitting MANY requests per second, we should strive to use a pool of HTTP Clients, instead of building new ones.
        HttpClient httpClient = getHttpClient(txId);

        logger.debug("[${txId}] Sending Attribute Query to EndpointAddress[@|cyan ${this.serverInfo?.getEndpointAddress()}|@]...");
        SOAPClient soapClient = new WSS4jHttpSOAPClient(httpClient, parserPool, this.clientInfo.getPrivateKey(), this.clientInfo.getCertificate());
        soapClient.send(serverInfo.getEndpointAddress(), inOutOperationContext);

        logger.debug("Validating the response...");
        // TODO analyze the response

        throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
    }//end attributeQuery()

    //==================================================================================================================
    //  Helper Methods
    //==================================================================================================================
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
        if ( webServiceRequestOptions )
           httpClientBuilder.setConnectionTimeToLive(this.webServiceRequestOptions.getNumber(WebServiceRequestOptions.HTTP_CLIENT_TIMEOUT, WebServiceRequestOptions.HTTP_CLIENT_TIMEOUT_DEFAULT), TimeUnit.SECONDS)
        else
           httpClientBuilder.setConnectionTimeToLive(WebServiceRequestOptions.HTTP_CLIENT_TIMEOUT_DEFAULT, TimeUnit.SECONDS)

        // TODO Figure out how to configure a custom SSL session to validate only against the server info given.


        //SSLContext sslContext = SSLContexts.createSystemDefault();
        //Generate a custom SSL Context based on server certificate provided during initialization
        KeyStore ks = buildKeyStore();
        KeyStore ts = buildTrustStore();
        SSLContextBuilder scb = new SSLContextBuilder();
        scb.create();
        scb.loadTrustMaterial (ts, new TrustSelfSignedStrategy());

        if( shouldUseClientCertInTLS() ) {
            logger.info ("Will attempt client certificate authentication, adding private key to the TLS context.");
            scb.loadKeyMaterial   (ks, "".toCharArray());
        } else {
            logger.info ("Will not attempt client certificate authentication.");
        }

        SSLContext sslContext = scb.build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        httpClientBuilder.setSSLContext(sslContext)  // Redundant with following line?
        httpClientBuilder.setSSLSocketFactory(sslsf)
        httpClientBuilder.setSSLHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
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

}/* end BAEServerImpl */
