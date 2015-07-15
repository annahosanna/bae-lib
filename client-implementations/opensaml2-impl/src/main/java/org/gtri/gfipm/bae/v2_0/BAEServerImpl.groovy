package org.gtri.gfipm.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.apache.commons.httpclient.HttpClient
import org.gtri.gfipm.bae.util.ClientKeyManager
import org.gtri.gfipm.bae.util.DirectX509TrustManager
import org.gtri.gfipm.bae.util.SAMLUtils
import org.gtri.gfipm.bae.util.WSS4jHttpSOAPClient
import org.gtri.gfipm.bae.util.WSS4jSecurityPolicyResolver
import org.joda.time.DateTime
import org.opensaml.DefaultBootstrap
import org.opensaml.common.SAMLVersion
import org.opensaml.saml2.core.Assertion
import org.opensaml.saml2.core.Attribute
import org.opensaml.saml2.core.AttributeQuery
import org.opensaml.saml2.core.AttributeStatement
import org.opensaml.saml2.core.Issuer
import org.opensaml.saml2.core.NameID
import org.opensaml.saml2.core.Response
import org.opensaml.saml2.core.Subject
import org.opensaml.saml2.core.impl.AttributeQueryBuilder
import org.opensaml.saml2.core.impl.IssuerBuilder
import org.opensaml.saml2.core.impl.NameIDBuilder
import org.opensaml.saml2.core.impl.SubjectBuilder
import org.opensaml.saml2.encryption.Decrypter
import org.opensaml.ws.security.SecurityPolicyResolver
import org.opensaml.ws.soap.client.BasicSOAPMessageContext
import org.opensaml.ws.soap.client.http.HttpClientBuilder
import org.opensaml.ws.soap.client.http.HttpSOAPRequestParameters
import org.opensaml.ws.soap.client.http.TLSProtocolSocketFactory
import org.opensaml.ws.soap.soap11.Body
import org.opensaml.ws.soap.soap11.Envelope
import org.opensaml.xml.Configuration
import org.opensaml.xml.XMLObject
import org.opensaml.xml.XMLObjectBuilderFactory
import org.opensaml.xml.encryption.DecryptionException
import org.opensaml.xml.encryption.InlineEncryptedKeyResolver
import org.opensaml.xml.io.Marshaller
import org.opensaml.xml.parse.BasicParserPool
import org.opensaml.xml.security.SecurityHelper
import org.opensaml.xml.security.keyinfo.KeyInfoGenerator
import org.opensaml.xml.security.keyinfo.StaticKeyInfoCredentialResolver
import org.opensaml.xml.security.x509.X509Credential
import org.opensaml.xml.signature.Signature
import org.opensaml.xml.signature.SignatureConstants
import org.opensaml.xml.signature.Signer
import org.opensaml.xml.util.XMLHelper
import org.w3c.dom.Element

import javax.xml.soap.SOAPException

class BAEServerImpl implements BAEServer {

    static Logger logger = LoggerFactory.get(BAEServerImpl)
    static String CLIENT_KEY_ALIAS = "mykey"

//    static {
//        logger.debug("Trying to initialize open saml...")
//        SAMLUtils.initialize();
//    }

    BAEClientInfo clientInfo
    BAEServerInfo serverInfo
    WebServiceRequestOptions webServiceRequestOptions


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

    @Override
    Collection<BackendAttribute> attributeQuery(SubjectIdentifier subjectId) throws BAEServerException {
        logger.info("Request to do attributeQuery on @|cyan ${subjectId}|@...")

        logger.debug("Building parserpool...")
        BasicParserPool parserPool = new BasicParserPool();
        parserPool.setNamespaceAware(true);

        logger.debug("Creating AttributeQuery...")
        AttributeQuery attributeQuery = buildAttributeQuery(subjectId)

        logger.debug("Creating client's X509Credential...")
        X509Credential clientTLSCred = getClientTLSCred();
        ClientKeyManager keyManager = new ClientKeyManager(clientInfo.privateKey, clientInfo.certificate);

        logger.debug("Signing AttributeQuery...");
        Signature signature = (Signature) Configuration.getBuilderFactory()
                .getBuilder(Signature.DEFAULT_ELEMENT_NAME)
                .buildObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setSigningCredential(clientTLSCred);
        signature.setSignatureAlgorithm(getStringValue(WebServiceRequestOptions.WSS_SIGNATURE_ALGORITHM, WebServiceRequestOptions.WSS_SIGNATURE_ALGORITHM_DEFAULT));
        signature.setCanonicalizationAlgorithm(getStringValue(WebServiceRequestOptions.WSS_CANONICALIZATION_ALGORITHM, WebServiceRequestOptions.WSS_CANONICALIZATION_ALGORITHM_DEFAULT));
        KeyInfoGenerator keyInfoGenerator = Configuration.getGlobalSecurityConfiguration().getKeyInfoGeneratorManager().getDefaultManager().getFactory(clientTLSCred).newInstance();
        signature.setKeyInfo(keyInfoGenerator.generate(clientTLSCred));
        attributeQuery.setSignature(signature);
        Configuration.getMarshallerFactory().getMarshaller(attributeQuery).marshall(attributeQuery);
        Signer.signObject(signature);

        logger.debug("Constructing SOAP Envelope...")
        Envelope envelope = buildSOAP11Envelope(attributeQuery);

        logger.debug("Building SOAP Message Context...")
        // SOAP context used by the SOAP client
        BasicSOAPMessageContext soapContext = new BasicSOAPMessageContext();
        soapContext.setOutboundMessage(envelope);
        SecurityPolicyResolver resolver = new WSS4jSecurityPolicyResolver(clientTLSCred.getPrivateKey(), clientTLSCred.getEntityCertificate(), serverInfo.certificates);
        soapContext.setSecurityPolicyResolver(resolver);
        HttpSOAPRequestParameters soapAction = new HttpSOAPRequestParameters("AttributeQuery");
        soapContext.setSOAPRequestParameters(soapAction);

        logger.debug("Setting Https ProtocolSocketFactory & Building HttpClient...")
        HttpClientBuilder clientBuilder = new HttpClientBuilder();
        if( shouldUseClientCertInTLS() ) {
            logger.debug("Setting https protocol socket factory with client & server keys...")
            TLSProtocolSocketFactory tlsSocketFactry = new TLSProtocolSocketFactory(keyManager, new DirectX509TrustManager(serverInfo.certificates, this.getWebServiceRequestOptions()))
            clientBuilder.setHttpsProtocolSocketFactory(tlsSocketFactry);
        }else{
            logger.warn("*** Client side https certificate validation & Direct X509TrustManager have been disabled ***")
        }
        logger.debug("Building HttpClient...")
        // TODO Else we should still configure DirectX509TrustManager for server.
        HttpClient httpClient = clientBuilder.buildClient();

        logger.debug("Creating SOAP Client...")
        WSS4jHttpSOAPClient soapClient = new WSS4jHttpSOAPClient(httpClient, parserPool, this.clientInfo.privateKey, this.clientInfo.certificate, serverInfo.certificates);

        // Send the message
        try {
            logger.debug("Sending message...");
            soapClient.send(this.serverInfo.endpointAddress, soapContext);
        } catch (SOAPException e) {
            logger.error("SOAP error sending message!", e)
            throw new BAEServerException(e);
        } catch (SecurityException e) {
            logger.error("Security error sending message!", e)
            throw new BAEServerException(e);
        }

        logger.debug("Checking response...");
        // Access the SOAP response envelope
        Envelope soapResponse = (Envelope) soapContext.getInboundMessage();
        Response response = getResponse(soapResponse)
        logger.info("Received response.  Status Code: "+response.getStatus().getStatusCode().getValue());

        logger.debug("Decrypting attributes...")
        // The EncryptedKey is assumed to be contained within the
        // EncryptedAssertion/EncryptedData/KeyInfo.
        Decrypter samlDecrypter = new Decrypter(null, new StaticKeyInfoCredentialResolver(clientTLSCred), new InlineEncryptedKeyResolver());

        Assertion assertion = null;
        response.getEncryptedAssertions().each { encryptedAssertion ->
            if( assertion == null ){
                logger.debug("Decrypting assertion...")
                try {
                    assertion = samlDecrypter.decrypt(encryptedAssertion);
                    logger.info("Decrypted Assertion: \n${XMLHelper.prettyPrintXML(assertion.getDOM())}")
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


        if( this.webServiceRequestOptions && this.webServiceRequestOptions.getBoolean(WebServiceRequestOptions.DUMP_RESPONSE_XML) ){
            String filePath = this.webServiceRequestOptions.getString(WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE, null);
            if( filePath ){
                logger.debug("Dumping XML to file[@|green ${filePath}|@]...")
                def file = new File(filePath)
                file.withPrintWriter { printWriter ->
                    printWriter.println(XMLHelper.prettyPrintXML(assertion.getDOM()));
                }
            }else{
                logger.warn("Found request to dump XML, but parameter 'WebServiceRequestOptions.DUMP_RESPONSE_XML_FILE' was empty.")
            }

        }//end


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


    private BackendAttribute convertAttribute( Attribute attribute ){
        BackendAttributeImpl beAttr = new BackendAttributeImpl()

        beAttr.name = attribute.getName()
        beAttr.friendlyName = attribute.friendlyName
        beAttr.nameFormat = AttributeNameFormat.fromUri(attribute.nameFormat)
        beAttr.values = convertToAttributeValues(attribute);

        return beAttr
    }//end convertAttribute()

    private List<BackendAttributeValue> convertToAttributeValues( XMLObject xmlObj ){
        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(xmlObj);
        if( marshaller == null )
            throw new NullPointerException("Could not marshal SAML XMLObject.  No marshaller found for "+xmlObj);
        Element element = marshaller.marshall(xmlObj);
        String xml = XMLHelper.prettyPrintXML(element);
        return BackendAttributeParser.parseValues(xml);
    }//end convertToString()

    private Response getResponse( Envelope soapResponse ){
        Response response = null;
        soapResponse.getBody().getUnknownXMLObjects().each{ xmlObject ->
            if( xmlObject instanceof Response )
                response = (Response) xmlObject;
        }
        return response
    }



    private AttributeQuery buildAttributeQuery( SubjectIdentifier subjectIdentifier ) {
        AttributeQueryBuilder attributeQueryBuilder = new AttributeQueryBuilder()
        AttributeQuery attributeQuery = attributeQueryBuilder.buildObject()
        attributeQuery.setDestination(this.serverInfo.destination)
        attributeQuery.setID(UUID.randomUUID().toString())
        attributeQuery.setIssueInstant(new DateTime())
        attributeQuery.setVersion(SAMLVersion.VERSION_20)

        IssuerBuilder issuerBuilder = new IssuerBuilder()
        Issuer issuer = issuerBuilder.buildObject()
        issuer.setValue(this.clientInfo.identifier)
        attributeQuery.setIssuer(issuer);

        SubjectBuilder subjectBuilder = new SubjectBuilder()
        Subject subject = subjectBuilder.buildObject()
        NameIDBuilder nameIDBuilder = new NameIDBuilder()
        NameID nameID = nameIDBuilder.buildObject()
        nameID.setFormat(subjectIdentifier.format)
        nameID.setValue(subjectIdentifier.name)
        subject.setNameID(nameID)
        attributeQuery.setSubject(subject);

        return attributeQuery;
    }


    private Envelope buildSOAP11Envelope(XMLObject payload) {
        XMLObjectBuilderFactory bf = Configuration.getBuilderFactory();
        Envelope envelope = (Envelope) bf.getBuilder(Envelope.DEFAULT_ELEMENT_NAME).buildObject(Envelope.DEFAULT_ELEMENT_NAME);
        Body body = (Body) bf.getBuilder(Body.DEFAULT_ELEMENT_NAME).buildObject(Body.DEFAULT_ELEMENT_NAME);

        body.getUnknownXMLObjects().add(payload);
        envelope.setBody(body);

        return envelope;
    }

    private X509Credential getClientTLSCred() {
        return SecurityHelper.getSimpleCredential(this.clientInfo.certificate, this.clientInfo.privateKey);
    }


}/* end BAEServerImpl */