package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import net.shibboleth.utilities.java.support.xml.SerializeSupport
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import org.apache.http.HttpEntity
import org.apache.http.client.HttpClient
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.ContentType
import org.apache.wss4j.common.WSEncryptionPart
import org.apache.wss4j.common.crypto.Crypto
import org.apache.wss4j.common.crypto.Merlin
import org.apache.wss4j.dom.WSConstants
import org.apache.wss4j.dom.message.WSSecHeader
import org.apache.wss4j.dom.message.WSSecSignature
import org.apache.wss4j.dom.message.WSSecTimestamp
import org.apache.wss4j.dom.util.WSSecurityUtil
import org.gtri.gfipm.bae.v2_0.BAEClientInfo
import org.gtri.gfipm.bae.v2_0.BAEServerInfo
import org.opensaml.core.config.ConfigurationService
import org.opensaml.core.xml.XMLObject
import org.opensaml.core.xml.config.XMLObjectProviderRegistry
import org.opensaml.core.xml.io.Marshaller
import org.opensaml.core.xml.io.MarshallingException
import org.opensaml.saml.saml2.core.AttributeQuery
import org.opensaml.soap.client.SOAPClientException
import org.opensaml.soap.client.http.HttpSOAPClient
import org.opensaml.soap.soap11.Envelope
import org.w3c.dom.Document
import org.w3c.dom.Element

import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.xml.transform.Result
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate

/**
 * Created by brad on 7/23/15.
 */
class WSS4jHttpSOAPClient extends HttpSOAPClient {
    //==================================================================================================================
    //  Statics
    //==================================================================================================================
    static Logger logger = LoggerFactory.get(WSS4jHttpSOAPClient);
    public static Integer DEFAULT_TTL = 300;
    //==================================================================================================================
    //  Constructors
    //==================================================================================================================
    public WSS4jHttpSOAPClient(HttpClient httpClient, ParserPool pPool, PrivateKey clientPrivateKey, X509Certificate clientCertificate){
        this.clientPrivateKey = clientPrivateKey;
        this.clientCertificate = clientCertificate;
        super.setHttpClient(httpClient);
        super.setParserPool(pPool);
        // TODO Set parser pool?
    }

    //==================================================================================================================
    //  Instance Variables
    //==================================================================================================================
    private Integer ttl = DEFAULT_TTL;
    private PrivateKey clientPrivateKey;
    private X509Certificate clientCertificate;
    //==================================================================================================================
    //  Overloaded Methods
    //==================================================================================================================
    @Override
    protected HttpEntity createRequestEntity(@Nonnull Envelope message, @Nullable Charset charset) throws SOAPClientException {
        String txId = null;
        // No idea what this is trying to accomplish, but it's doing a get(0) on an empty list, so protect against that. jk
        if ( ! message.getUnknownXMLObjects().isEmpty () ) {
          XMLObject messageBody = message.getUnknownXMLObjects().get(0);
          if( messageBody instanceof AttributeQuery ){
             txId = ((AttributeQuery) messageBody).getID();
          }else{
             txId = "create-entity-${UUID.randomUUID().toString().replace("-", "")}";
          }
        }

        try {
            logger.debug("[${txId}] Marshalling to XML...")
            XMLObjectProviderRegistry xmlObjectProviderRegistry = ConfigurationService.get(XMLObjectProviderRegistry.class);
            Marshaller marshaller = xmlObjectProviderRegistry.getMarshallerFactory().getMarshaller(message);
            Element soapEnvelopeElement = marshaller.marshall(message);
            logger.debug("[${txId}] Outbound SOAP message is [BEFORE WSS4J]:\n %s", SerializeSupport.prettyPrintXML(soapEnvelopeElement));

            logger.debug("[${txId}] Inserting WSS4J security headers...");
            Document xml = soapEnvelopeElement.getOwnerDocument();
            WSSecHeader secHeader = new WSSecHeader(xml);
            secHeader.insertSecurityHeader();

            logger.debug("[${txId}] Inserting timestamp...")
            WSSecTimestamp timestamp = new WSSecTimestamp();
            timestamp.setTimeToLive(this.ttl);
            xml = timestamp.build(xml, secHeader);

            logger.debug("[${txId}] Building parts for signature...")
            WSEncryptionPart timestampEncPart = new WSEncryptionPart(timestamp.getId());
            String soapNamespace = WSSecurityUtil.getSOAPNamespace(xml.getDocumentElement());
            WSEncryptionPart bodyPart = new WSEncryptionPart(WSConstants.ELEM_BODY, soapNamespace, "Content")

            logger.debug("[${txId}] Building Crypto Implementation...")
            Crypto crypto = this.buildCrypto();
            logger.debug("[${txId}] Resutling Crypto: " + crypto)

            logger.debug("[${txId}] Inserting signature...")
            WSSecSignature signature = new WSSecSignature();
            signature.setUserInfo("myKey", "");
            signature.prepare(xml, crypto, secHeader);
            signature.getParts().add(timestampEncPart);
            signature.getParts().add(bodyPart);
            // Possible values are: SKI_KEY_IDENTIFIER, BST_DIRECT_REFERENCE, X509_KEY_IDENTIFIER & ISSUER_SERIAL.  Note that X509_KEY_IDENTIFIER causes an error on their side though.
            //    Using a value WSConstants.BST_DIRECT_REFERENCE causes it to put in a Binary Security Token Reference.
            signature.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
            xml = signature.build(xml, crypto, secHeader);

            logger.debug("[${txId}] Marshalling back out with charset (" + charset + ")...");
            byte[] xmlBytes = toBytes(xml, charset);
            logger.debug("[${txId}] Outbound SOAP message is [AFTER WSS4J]:\n %s", SerializeSupport.prettyPrintXML(xml.getDocumentElement()));

            return new ByteArrayEntity(xmlBytes, ContentType.create("text/xml", "utf-8"));
        } catch (MarshallingException e) {
            throw new SOAPClientException("[${txId}] Unable to marshall SOAP envelope", e);
        }
    }

    //==================================================================================================================
    //  Private Helper Methods
    //==================================================================================================================
    /**
     * Creates a WSS4j Merlin Crypto implementation based on the client's credentials.
     */
    private Crypto buildCrypto(){
        if( this.clientPrivateKey == null )
            throw new NullPointerException("Cannot build required WSS4j Crypto, since 'ClientPrivateKey' is null.")
        if( this.clientCertificate == null )
            throw new NullPointerException("Cannot build required WSS4j Crypto, since 'ClientCertificate' is null.")
        Merlin merlin = new Merlin();
        char[] pw = "".toCharArray();
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null); // Creates an empty keystore
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(pw);
        //javax.crypto.SecretKey mySecretKey = this.clientPrivateKey;
        //List<X509Certificate> clientCerts = [];
        //clientCerts.add (this.clientCertificate);
        //logger.debug ("Private Key: " + clientPrivateKey.toString());
        //logger.debug ("Private Key: " + clientCertificate.toString());
        KeyStore.PrivateKeyEntry pkEntry = new KeyStore.PrivateKeyEntry(clientPrivateKey, clientCertificate);
        //logger.debug ("KeyStore: " + pkEntry);
        keyStore.setEntry("myKey", pkEntry, protParam);
        keyStore.setCertificateEntry("myCert", this.clientCertificate);
        merlin.setKeyStore(keyStore);
        return merlin;
    }//end buildCrypto()

    private byte[] toBytes(Document document, Charset charset){
        Source source = new DOMSource(document);
        StringWriter xmlStringWriter = new StringWriter();
        Result result = new StreamResult(xmlStringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);
        xmlStringWriter.flush();
        String xmlString = xmlStringWriter.toString();
        return xmlString.getBytes(charset);
    }//end toBytes()



}//end WSS4jHttpSOAPClient
