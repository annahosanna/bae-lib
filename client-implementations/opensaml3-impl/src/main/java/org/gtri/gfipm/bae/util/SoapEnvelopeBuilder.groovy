package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.opensaml.core.config.ConfigurationService
import org.opensaml.core.xml.XMLObjectBuilderFactory
import org.opensaml.core.xml.config.XMLObjectProviderRegistry
import org.opensaml.messaging.context.MessageContext
import org.opensaml.soap.messaging.context.SOAP11Context
import org.opensaml.soap.soap11.Body
import org.opensaml.soap.soap11.Envelope

/**
 * Created by brad on 7/17/15.
 */
class SoapEnvelopeBuilder {

    static Logger logger = LoggerFactory.get(SoapEnvelopeBuilder.class)

    public static Envelope buildSoap11Envelope(MessageContext messageContext){
        logger.debug("[${messageContext.getMessage()?.getID()}] Wrapping XMLObject @|green ${messageContext.getMessage()}|@ in a SOAP Envelope...")

        XMLObjectProviderRegistry xmlObjectProviderRegistry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        XMLObjectBuilderFactory xmlObjectBuilderFactory = xmlObjectProviderRegistry?.getBuilderFactory();
        if( xmlObjectBuilderFactory == null )
            throw new NullPointerException("Unable to obtain instance of '${XMLObjectBuilderFactory.class.name}' from the OpenSAML ConfigurationService.")

        Envelope envelope = xmlObjectBuilderFactory.getBuilder(Envelope.DEFAULT_ELEMENT_NAME).buildObject(Envelope.DEFAULT_ELEMENT_NAME);
        Body body = xmlObjectBuilderFactory.getBuilder(Body.DEFAULT_ELEMENT_NAME).buildObject(Body.DEFAULT_ELEMENT_NAME);
        body.getUnknownXMLObjects().add(messageContext.getMessage());
        envelope.setBody(body);

        messageContext.getSubcontext(SOAP11Context.class, true).setEnvelope(envelope);

        return envelope;
    }//end buildSoap11Envelope()

}/* end class SoapEnvelopeBuilder */