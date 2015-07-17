package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.gtri.gfipm.bae.v2_0.SubjectIdentifier
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

/**
 * Responsible for building a suitable {@link AttributeQuery} which can be enveloped by SOAP and sent to a BAE server.
 * <br/><br/>
 * Created by brad on 7/17/15.
 */
class AttributeQueryBuilder {

    static Logger logger = LoggerFactory.get(AttributeQueryBuilder.class)

    /**
     * Builds the query in a manner compliant with the OpenSAML 3 Libraries.
     * <br/><br/>
     * @param subjectId The subject for this attibute query
     * @param transactionId the unique identifier for this transaction (must be locally [ie, JVM] unique, should be globally unique.  Consider soemthing like UUID
     * @param destination The destination identifier for this query
     * @param issuerIdentifier the issuer identifier for this query (must be understood by the remote BAE server)
     * @return an OpenSAML 3 AttributeQuery object.
     */
    public static AttributeQuery build(SubjectIdentifier subjectId, String transactionId, String destination, String issuerIdentifier) {
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
        attributeQuery.setDestination(destination);
        attributeQuery.setID(transactionId);
        attributeQuery.setIssueInstant(new DateTime());
        attributeQuery.setVersion(SAMLVersion.VERSION_20);

        XMLObjectBuilder<Issuer> issuerBuilder = xmlObjectBuilderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
        if( issuerBuilder == null )
            throw new NullPointerException("Unable to obtain instance of ${XMLObjectBuilder.class.simpleName}<${Issuer.class.simpleName}> from OpenSAML ${XMLObjectBuilderFactory.class.name}")

        Issuer issuer = issuerBuilder.buildObject(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue(issuerIdentifier);

        attributeQuery.setIssuer(issuer);

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

        logger.info("Successfully built AttributeQuery[id=@|cyan ${attributeQuery?.getID()}|@] around subject @|green ${subjectId}|@")
        return attributeQuery;
    }//end build()

    /**
     * Simply prints out all registered XMLObjectBuilder instances that OpenSAML has registered, mainly for the purpose
     * of debugging and figuring out what to use.
     */
    private static void printObjectBuilders(XMLObjectBuilderFactory xmlObjectBuilderFactory ){
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


}//end AttributeQueryBuilder