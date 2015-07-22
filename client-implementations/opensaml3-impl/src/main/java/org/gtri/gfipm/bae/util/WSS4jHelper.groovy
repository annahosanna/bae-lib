package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.apache.wss4j.dom.WSConstants
import org.apache.wss4j.dom.message.WSSecHeader
import org.apache.wss4j.dom.message.WSSecSignature
import org.apache.wss4j.dom.message.WSSecTimestamp
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * When given a SOAP Envelope (with no Security Headers) this method will output the SOAP envelope with BAE 2.0 compliant
 * security headers.
 * <br/><br/>
 * Created by brad on 7/22/15.
 */
class WSS4jHelper {
    //==================================================================================================================
    //  Static Variables/Constants
    //==================================================================================================================
    public static final Logger logger = LoggerFactory.get(WSS4jHelper.class);

    //==================================================================================================================
    //  Static Methods
    //==================================================================================================================
    /**
     * Adds the BAE 2.0 Compliant SOAP headers (timestamp, signature) to the given soap11Envelope, returning the new
     * Envelope.
     * <br/><br/>
     * @param soap11Envelope
     * @return
     */
    public static Element addWSSecurity( Element soap11Envelope ){
        logger.debug("Invoked addWSSecurity()...");

        Document ownerDoc = soap11Envelope.getOwnerDocument();

        WSSecHeader secHeader = new WSSecHeader(ownerDoc);
        secHeader.insertSecurityHeader();

        WSSecTimestamp timestamp = new WSSecTimestamp(); // defaults to TTL of 300
        Document timestampedDoc = timestamp.build(ownerDoc, secHeader);

        // Now we sign the document.
        WSSecSignature signatureBuilder = new WSSecSignature();
        signatureBuilder.setKeyIdentifierType(WSConstants.ISSUER_SERIAL);



    }//end addWSSecurity(Element)


    //==================================================================================================================
    //  Helper Methods
    //==================================================================================================================


}//end class WSS4jHelper