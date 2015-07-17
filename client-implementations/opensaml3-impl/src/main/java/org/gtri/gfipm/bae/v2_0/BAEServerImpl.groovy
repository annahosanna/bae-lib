package org.gtri.gfipm.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.gtri.gfipm.bae.util.AttributeQueryBuilder
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

        AttributeQuery attributeQuery = AttributeQueryBuilder.build(subjectId, UUID.randomUUID().toString(), this.getDestination(), this.getIssuerIdentifier());
        logger.info("Successfully built AttributeQuery[id=@|green ${attributeQuery.getID()}|@] for @|cyan ${subjectId}|@");

        // Time to implement the enveloped signature,
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

    private String getDestination() {
        // TODO FIXME Need to pull this from the server info
        return "urn:dhs.gov:icam:bae:v1.0:test";
    }

    private String getIssuerIdentifier() {
        // TODO FIXME Need to pull this from the client info
        return "URN:TEST:ICAM:BAE:V2:GTRI";
    }

}/* end BAEServerImpl */