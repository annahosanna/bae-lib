package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.apache.xml.security.Init
import org.opensaml.DefaultBootstrap
import org.opensaml.saml2.core.AttributeQuery
import org.opensaml.saml2.core.Response
import org.opensaml.xml.Configuration
import org.opensaml.xml.io.Unmarshaller
import org.opensaml.xml.io.UnmarshallerFactory
import org.opensaml.xml.parse.BasicParserPool
import org.opensaml.xml.security.BasicSecurityConfiguration
import org.opensaml.xml.security.SecurityConfiguration
import org.opensaml.xml.signature.SignatureConstants
import org.w3c.dom.Document
import org.w3c.dom.Element

class SAMLUtils {

    static Logger logger = LoggerFactory.get(SAMLUtils.class)


    /**
     * Checks the OpenSAML library configuration to see if it is already initialized or not.
     * @return true if already initialized, false if not.
     */
    public static Boolean isInitiailized() {
        // The Init class is called by the DefaultBootstrap.bootstrap() -> initializeXMLSecurity() method.  It should be sufficient to determine if initialization has occurred.
        return Init.isInitialized()
    }//end isInitiailized


    public static void initialize() {
        if( !isInitiailized() ){
            logger.warn("Initializing OpenSAML Library...")
            try{
                DefaultBootstrap.bootstrap();
            }catch(Throwable t){
                logger.error("Error initializing OpenSAML Library!", t);
                throw new UnsupportedOperationException("Cannot initialize OpenSAML!", t);
            }

            // @see https://iead.ittl.gtri.org/jira/browse/PNB-22
            SecurityConfiguration config = Configuration.getGlobalSecurityConfiguration();
            if (config instanceof BasicSecurityConfiguration) {
                BasicSecurityConfiguration secConfig =
                    (BasicSecurityConfiguration) Configuration.getGlobalSecurityConfiguration();


                logger.info("Overriding global OpenSAML digital signature algoriithm URI (RSA with SHA1) to: {}",
                        SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);

                secConfig.registerSignatureAlgorithmURI("RSA", SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
            }


        }
    }


}
