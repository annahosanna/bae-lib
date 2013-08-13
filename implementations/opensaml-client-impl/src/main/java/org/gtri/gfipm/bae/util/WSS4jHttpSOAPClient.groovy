package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity
import org.apache.commons.httpclient.methods.RequestEntity
import org.apache.ws.security.WSConstants
import org.apache.ws.security.WSEncryptionPart
import org.apache.ws.security.components.crypto.Crypto
import org.apache.ws.security.message.WSSecHeader
import org.apache.ws.security.message.WSSecSignature
import org.apache.ws.security.message.WSSecTimestamp
import org.apache.ws.security.util.WSSecurityUtil
import org.opensaml.ws.soap.client.SOAPClientException
import org.opensaml.ws.soap.client.http.HttpSOAPClient
import org.opensaml.ws.soap.soap11.Envelope
import org.opensaml.xml.Configuration
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException
import org.opensaml.xml.parse.ParserPool
import org.opensaml.xml.util.XMLHelper
import org.w3c.dom.Document

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Result
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.nio.charset.Charset
import java.security.PrivateKey
import java.security.cert.X509Certificate

/**
 * This extension of the OpenSAML {@link HttpSOAPClient} provides for adding WSSecurity headers using WSS4j.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/04/19 2:31 PM
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
    WSS4jHttpSOAPClient(HttpClient client, ParserPool parser, PrivateKey newPrivateKey, X509Certificate newCert, Collection<X509Certificate> serverCertificates) {
        super(client, parser)
        this.privateKey = newPrivateKey
        this.certificate = newCert
    }
    //==================================================================================================================
    //  Instance Variables
    //==================================================================================================================
    PrivateKey privateKey;
    X509Certificate certificate;
    Collection<X509Certificate> serverCertificates;
    Integer ttl = DEFAULT_TTL;
    //==================================================================================================================
    //  Protected Methods
    //==================================================================================================================
    protected Document toDocument(byte[] bytes){
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        docBuilderFactory.setIgnoringComments(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        return docBuilder.parse(new ByteArrayInputStream(bytes));
    }//end toDocument()

    protected byte[] toBytes( Document document, Charset charset ){
        Source source = new DOMSource(document);
        StringWriter xmlStringWriter = new StringWriter();
        Result result = new StreamResult(xmlStringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);
        xmlStringWriter.flush();
        String xmlString = xmlStringWriter.toString();
        return xmlString.getBytes(charset);
    }//end toBytes
    //==================================================================================================================
    //  Public Methods
    //==================================================================================================================
    /*
    Crypto Properties Listing:
      org.apache.ws.security.crypto.provider=org.apache.ws.security.components.crypto.Merlin

     */
    protected Crypto buildCrypto(){
        return new XMLCryptoHelperClient(this.getPrivateKey(), this.getCertificate());
    }

    //==================================================================================================================
    //  Overridden Methods
    //==================================================================================================================
    @Override
    protected RequestEntity createRequestEntity(Envelope message, Charset charset) throws SOAPClientException {
        try {
            logger.debug("Marshalling to XML...")
            Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(message);
            ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(arrayOut, charset);
            XMLHelper.writeNode(marshaller.marshall(message), writer);
            byte[] xmlBytes = arrayOut.toByteArray();
            logger.debug("Outbound SOAP message is [BEFORE WSS4J]:\n %s", new String(xmlBytes, charset));

            logger.debug("Inserting WSS4J security headers...");
            Document xml = toDocument(xmlBytes);
            xmlBytes = null;
            WSSecHeader secHeader = new WSSecHeader();
            secHeader.insertSecurityHeader(xml);

            logger.debug("Inserting timestamp...")
            WSSecTimestamp timestamp = new WSSecTimestamp();
            timestamp.setTimeToLive(this.getTtl());
            xml = timestamp.build(xml, secHeader);

            logger.debug("Building parts for signature...")
            WSEncryptionPart timestampEncPart = new WSEncryptionPart(timestamp.getId());
            String soapNamespace = WSSecurityUtil.getSOAPNamespace(xml.getDocumentElement());
            WSEncryptionPart bodyPart = new WSEncryptionPart(WSConstants.ELEM_BODY, soapNamespace, "Content")

            Crypto crypto = this.buildCrypto();

            logger.debug("Inserting signature...")
            WSSecSignature signature = new WSSecSignature();
            signature.prepare(xml, crypto, secHeader);
            signature.setParts([timestampEncPart, bodyPart]);
            // Possible values are: SKI_KEY_IDENTIFIER, BST_DIRECT_REFERENCE, X509_KEY_IDENTIFIER & ISSUER_SERIAL.  Note that X509_KEY_IDENTIFIER causes an error on their side though.
            //    Using a value WSConstants.BST_DIRECT_REFERENCE causes it to put in a Binary Security Token Reference.
            signature.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
            xml = signature.build(xml, crypto, secHeader);

            logger.debug("Marshalling back out...");
            xmlBytes = toBytes(xml, charset);
            logger.debug("Outbound SOAP message is [AFTER WSS4J]:\n %s", new String(xmlBytes, charset));

            return new ByteArrayRequestEntity(xmlBytes, "text/xml");
        } catch (MarshallingException e) {
            throw new SOAPClientException("Unable to marshall SOAP envelope", e);
        }
    }//end createRequestEntity()





}//end ExtendedHttpSOAPClient()