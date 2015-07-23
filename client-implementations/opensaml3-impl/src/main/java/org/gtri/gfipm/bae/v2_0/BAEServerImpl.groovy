package org.gtri.gfipm.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.apache.http.client.HttpClient
import org.apache.http.conn.HttpClientConnectionManager
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.gtri.gfipm.bae.util.AttributeQueryBuilder
import org.gtri.gfipm.bae.util.AttributeQuerySigner
import org.gtri.gfipm.bae.util.SoapEnvelopeBuilder
import org.gtri.gfipm.bae.util.WSS4jHttpSOAPClient
import org.opensaml.messaging.context.InOutOperationContext
import org.opensaml.messaging.context.MessageContext
import org.opensaml.saml.saml2.core.AttributeQuery
import org.opensaml.soap.client.SOAPClient
import org.opensaml.soap.messaging.context.SOAP11Context
import org.opensaml.soap.soap11.Envelope
import org.opensaml.xmlsec.signature.Signature

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

        logger.info("[${txId}] Request to do attributeQuery on @|cyan ${subjectId}|@...")
        validateConfiguration();

        logger.debug("[${txId}] Building AttributeQuery Object...");
        AttributeQuery attributeQuery = AttributeQueryBuilder.build(subjectId, txId, this.getDestination(), this.getIssuerIdentifier());

        logger.debug("[${txId}] Sending AttributeQuery to be signed...");
        Signature attributeQuerySignature = AttributeQuerySigner.sign(attributeQuery, clientInfo);

        logger.debug("[${txId}] Building SOAP message contexts...");
        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(attributeQuery);
        Envelope envelope = SoapEnvelopeBuilder.buildSoap11Envelope(messageContext);
        MessageContext inboundSoapContext = new MessageContext();
        inboundSoapContext.addSubcontext(SOAP11Context.class, true);
        InOutOperationContext inOutOperationContext = new InOutOperationContext(inboundSoapContext, messageContext);

        // TODO When submitting MANY requests per second, we should strive to use a pool of HTTP Clients, instead of building new ones.
        HttpClient httpClient = getHttpClient(txId);

        logger.debug("[${txId}] Sending Attribute Query to EndpointAddress[@|cyan ${this.serverInfo?.getEndpointAddress()}|@]...");
        SOAPClient soapClient = new WSS4jHttpSOAPClient(httpClient, this.clientInfo.getPrivateKey(), this.clientInfo.getCertificate());
        soapClient.send(this.serverInfo?.endpointAddress, inOutOperationContext);

        logger.debug("Validating the response...");
        // TODO analyze the response

        throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
    }//end attributeQuery()

    //==================================================================================================================
    //  Helper Methods
    //==================================================================================================================
    /**
     * This method is called to check the configuration before performing any work.  If the configuration is found to
     * be invalid, a simple exception is raised with the details of what is invalid.
     */
    private void validateConfiguration() throws BAEServerException {

        // TODO Check serverInfo & clientInfo

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
        // TODO FIXME Need to pull this from the server info
        return "urn:dhs.gov:icam:bae:v1.0:test";
    }

    private String getIssuerIdentifier() {
        // TODO FIXME Need to pull this from the client info
        return "URN:TEST:ICAM:BAE:V2:GTRI";
    }

    private CloseableHttpClient getHttpClient(String txId) {
        logger.debug("[$txId] Building an HTTP Client...");
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
        httpClientBuilder.setConnectionTimeToLive(this.webServiceRequestOptions.getNumber(WebServiceRequestOptions.HTTP_CLIENT_TIMEOUT, WebServiceRequestOptions.HTTP_CLIENT_TIMEOUT_DEFAULT), TimeUnit.SECONDS)

        // TODO Figure out how to configure a custom SSL session to validate only against the server info given.

        if( shouldUseClientCertInTLS() ){
            // TODO We should present the client certificate to initiate a TLS connection.


        }

        SSLContext sslContext = SSLContexts.createSystemDefault();
        httpClientBuilder.setSSLContext(sslContext)
//        httpClientBuilder.setSSLHostnameVerifier(HostnameVerifier hostnameVerifier)


        return httpClientBuilder.build();
    }

}/* end BAEServerImpl */