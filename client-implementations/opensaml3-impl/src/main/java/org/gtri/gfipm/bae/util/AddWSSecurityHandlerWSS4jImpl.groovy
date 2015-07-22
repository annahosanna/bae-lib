package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import net.shibboleth.utilities.java.support.xml.SerializeSupport
import org.opensaml.core.config.ConfigurationService
import org.opensaml.core.xml.config.XMLObjectProviderRegistry
import org.opensaml.messaging.context.MessageContext
import org.opensaml.messaging.handler.AbstractMessageHandler
import org.opensaml.messaging.handler.MessageHandlerException
import org.opensaml.soap.messaging.context.SOAP11Context
import org.opensaml.soap.soap11.Envelope
import org.w3c.dom.Element

import javax.annotation.Nonnull;

/**
 * Uses WSS4j to add Client Signature and Timestamp to document in a manner consistent with the BAE 2.0 spec.
 *
 * Inspired from:
 *  http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-soap-impl/src/main/java/org/opensaml/soap/wssecurity/messaging/impl/AddTimestampHandler.java?view=markup
 *  http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-soap-impl/src/test/java/org/opensaml/soap/wssecurity/messaging/impl/AddTimestampHandlerTest.java?view=markup
 * <br/><br/>
 * Created by brad on 7/22/15.
 */
class AddWSSecurityHandlerWSS4jImpl extends AbstractMessageHandler  {
    //==================================================================================================================
    //  Static Variables/Constants
    //==================================================================================================================
    private static final Logger logger = LoggerFactory.get(AddWSSecurityHandlerWSS4jImpl.class);
    //==================================================================================================================
    // AbstractMessageHandler Implementation
    //==================================================================================================================
    @Override
    protected void doInvoke(@Nonnull MessageContext messageContext) throws MessageHandlerException {
        SOAP11Context soap11Context = messageContext.getSubcontext(SOAP11Context.class);
        if( soap11Context == null )
            throw new UnsupportedOperationException("Exepcting to find a SOAP11Context on the MessageContext, but failed.  Cannot sign this non-SOAP context.");

        Envelope soapEnvelope = soap11Context.getEnvelope();
        XMLObjectProviderRegistry xmlObjectProviderRegistry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        Element soapEnvelopeElement = xmlObjectProviderRegistry.getMarshallerFactory().getMarshaller(soapEnvelope).marshall(soapEnvelope);

        logger.info("Adding WS-Security Timestamp and Signature headers (with WSS4j) to SOAP document: \n"+SerializeSupport.prettyPrintXML(soapEnvelopeElement));



    }//end doInvoke()

    //==================================================================================================================
    // Helper Methods
    //==================================================================================================================


}//end AddWSSecurityHandlerWSS4jImpl
