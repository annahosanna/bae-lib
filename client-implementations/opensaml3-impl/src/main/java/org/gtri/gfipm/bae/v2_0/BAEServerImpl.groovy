package org.gtri.gfipm.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.joda.time.DateTime
import org.opensaml.core.config.ConfigurationService
import org.opensaml.core.xml.XMLObjectBuilder
import org.opensaml.core.xml.XMLObjectBuilderFactory
import org.opensaml.core.xml.config.XMLObjectProviderRegistry
import org.opensaml.saml.common.SAMLVersion
import org.opensaml.saml.saml2.core.AttributeQuery
import org.opensaml.saml.saml2.core.Issuer
import org.opensaml.saml.saml2.core.NameID
import org.opensaml.saml.saml2.core.Subject

import javax.xml.namespace.QName

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
        logger.info("Request to do attributeQuery on @|cyan ${subjectId}|@...")
        validateConfiguration();

        AttributeQuery attributeQuery = buildAttributeQuery(subjectId);
        logger.info("Successfully built AttributeQuery[id=@|green ${attributeQuery.getID()}|@] for @|cyan ${subjectId}|@");

        // Time to implement the envoloped signature,
        //    @see http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-xmlsec-impl/src/test/java/org/opensaml/xmlsec/signature/support/EnvelopedSignatureTest.java?view=markup


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

    private String getAttributeQueryDestination() {
        return "urn:dhs.gov:icam:bae:v1.0:test";
    }

    private String getClientInfoIdentifier() {
        return "URN:TEST:ICAM:BAE:V2:GTRI";
    }

    /**
     * Uses the Open SAML 3 {@link XMLObjectBuilder} APIs to build a valid {@link AttributeQuery} object.  This method simply
     * uses UUID.randomUUID() to build a transaction id.
     */
    protected AttributeQuery buildAttributeQuery(SubjectIdentifier subjectId){
        return buildAttributeQuery(subjectId, UUID.randomUUID().toString());
    }
    /**
     * Uses the Open SAML 3 {@link XMLObjectBuilder} APIs to build a valid {@link AttributeQuery} object.
     */
    protected AttributeQuery buildAttributeQuery(SubjectIdentifier subjectId, String transactionId){
        XMLObjectProviderRegistry xmlObjectProviderRegistry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        XMLObjectBuilderFactory xmlObjectBuilderFactory = xmlObjectProviderRegistry?.getBuilderFactory();
        if( xmlObjectBuilderFactory == null )
            throw new NullPointerException("Unable to obtain instance of '${XMLObjectBuilderFactory.class.name}' from the OpenSAML ConfigurationService.")

//        printObjectBuilders(xmlObjectBuilderFactory);

        XMLObjectBuilder<AttributeQuery> attributeQueryBuilder = xmlObjectBuilderFactory.getBuilder(AttributeQuery.DEFAULT_ELEMENT_NAME);
        if( attributeQueryBuilder == null )
            throw new NullPointerException("Unable to obtain instance of ${XMLObjectBuilder.class.simpleName}<${AttributeQuery.class.simpleName}> from OpenSAML ${XMLObjectBuilderFactory.class.name}")

        logger.debug("Building @|cyan ${AttributeQuery.class.simpleName}|@ object...");
        AttributeQuery attributeQuery = attributeQueryBuilder.buildObject(AttributeQuery.DEFAULT_ELEMENT_NAME);
        attributeQuery.setDestination(this.getAttributeQueryDestination());
        attributeQuery.setID(transactionId);
        attributeQuery.setIssueInstant(new DateTime());
        attributeQuery.setVersion(SAMLVersion.VERSION_20);

        XMLObjectBuilder<Issuer> issuerBuilder = xmlObjectBuilderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
        if( issuerBuilder == null )
            throw new NullPointerException("Unable to obtain instance of ${XMLObjectBuilder.class.simpleName}<${Issuer.class.simpleName}> from OpenSAML ${XMLObjectBuilderFactory.class.name}")

        Issuer issuer = issuerBuilder.buildObject(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue(this.getClientInfoIdentifier());

        XMLObjectBuilder<Subject> subjectBuilder = xmlObjectBuilderFactory.getBuilder(Subject.DEFAULT_ELEMENT_NAME);
        if( subjectBuilder == null )
            throw new NullPointerException("Unable to obtain instance of ${XMLObjectBuilder.class.simpleName}<${Subject.class.simpleName}> from OpenSAML ${XMLObjectBuilderFactory.class.name}")

        Subject subject = subjectBuilder.buildObject(Subject.DEFAULT_ELEMENT_NAME);

        XMLObjectBuilder<NameID> nameIDBuilder = xmlObjectBuilderFactory.getBuilder(NameID.DEFAULT_ELEMENT_NAME);
        if( nameIDBuilder == null )
            throw new NullPointerException("Unable to obtain instance of ${XMLObjectBuilder.class.simpleName}<${NameID.class.simpleName}> from OpenSAML ${XMLObjectBuilderFactory.class.name}")

        NameID nameId = nameIDBuilder.buildObject(NameID.DEFAULT_ELEMENT_NAME);
        nameId.setFormat(subjectId?.getFormat());
        nameId.setValue(subjectId?.getName());

        subject.setNameID(nameId);

        attributeQuery.setSubject(subject);

        return attributeQuery;
    }

    /**
     * Simply prints out all registered XMLObjectBuilder instances that OpenSAML has registered, mainly for the purpose
     * of debugging and figuring out what to use.
     */
    private void printObjectBuilders(XMLObjectBuilderFactory xmlObjectBuilderFactory ){
        StringBuilder builder = new StringBuilder();
        builder.append("XML Object Builders registered in OpenSAML 3: \n")
        Set<QName> qnames = xmlObjectBuilderFactory.builders.keySet();
        List<QName> sortedQNames = []
        sortedQNames.addAll(qnames);
        Collections.sort(sortedQNames, { QName q1, QName q2 -> return q1.localPart.compareToIgnoreCase(q2.localPart); } as Comparator);
        sortedQNames.each { qname ->
            builder.append("    [${qname.namespaceURI}]: ${qname.localPart}\n");
        }
        logger.info(builder.toString());
    }

}/* end BAEServerImpl */