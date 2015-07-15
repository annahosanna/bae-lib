package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.apache.ws.security.WSConstants
import org.apache.ws.security.WSSecurityEngine
import org.apache.ws.security.WSSecurityEngineResult
import org.apache.ws.security.WSSecurityException
import org.apache.ws.security.components.crypto.Crypto
import org.opensaml.ws.message.MessageContext
import org.opensaml.ws.security.SecurityPolicyException
import org.opensaml.ws.security.SecurityPolicyRule
import org.opensaml.ws.soap.soap11.Envelope
import org.w3c.dom.Element

import java.security.PrivateKey
import java.security.cert.X509Certificate

/**
 * Class acutally responsible for implementing the WSS4j Security header negotiation in SAML.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/04/24 1:01 PM
 */
class WSS4jSecurityPolicyRule implements SecurityPolicyRule {
    //==================================================================================================================
    //  Static Variables
    //==================================================================================================================
    static Logger logger = LoggerFactory.get(WSS4jSecurityPolicyRule)
    //==================================================================================================================
    //  Constructors
    //==================================================================================================================
    public WSS4jSecurityPolicyRule(Boolean force, PrivateKey newPrivateKey, X509Certificate clientCertificate, Collection<X509Certificate> serverCertificates){
        this.force = force
        this.privateKey = newPrivateKey
        this.clientCertificate = clientCertificate
        this.serverCertificates = serverCertificates
    }
    //==================================================================================================================
    //  Instance Variables
    //==================================================================================================================
    Boolean force
    PrivateKey privateKey
    X509Certificate clientCertificate
    Collection<X509Certificate> serverCertificates
    //==================================================================================================================
    //  Helper Methods
    //==================================================================================================================
    public Element getSecurityHeader( Envelope soapResponse ){
        // TODO a better a job.
        Element header = soapResponse.getHeader().getUnknownXMLObjects().get(0).getDOM();
        return header
    }//end getSecurityHeader()

    /**
     * Processes the {@link Envelope} to see if there is any SOAP Headers present.  If so, then the method returns true.
     * If not, then the message returns false.
     * <br/><br/>
     * @param context the {@link Envelope} to process
     * @return true if there is a ws security header, false otherwise.
     */
    public Boolean containsWSSHeader(Envelope soapResponse){
        // TODO
        return Boolean.FALSE;
    }//end containsWSSHeader()
    //==================================================================================================================
    //  Interface Implementations
    //==================================================================================================================
    @Override
    void evaluate(MessageContext messageContext) throws SecurityPolicyException {
        Envelope soapResponse = (Envelope) messageContext.getInboundMessage();

        logger.debug("Evaluating WSS4j WSSecurity...")
        WSSecurityEngine secEngine = new WSSecurityEngine();
        Crypto crypto = new XMLCryptoHelperServer(this.getServerCertificates());
        Element securityHeader = getSecurityHeader(soapResponse)
        List<WSSecurityEngineResult> results = []
        try {
            results = secEngine.processSecurityHeader(securityHeader, null, crypto, crypto);
        }catch( WSSecurityException wsse ){
            logger.error("Error processing WS Security!", wsse);
            throw wsse;
        }
        if( results == null || results.isEmpty() ){
            logger.warn "No WSSecurity results!"
        }else{
            logger.info("Processing the following security results(@|cyan ${results?.size() ?: 0}|@):")
            results?.each{ WSSecurityEngineResult result ->
                logger.info("    Result[${result.getClass().getName()}]: ${result.toString()}")
            }
            results.each{ WSSecurityEngineResult result ->
                Integer action = result.get(WSSecurityEngineResult.TAG_ACTION)
                String actionName = "[UNKNOWN: $action]"
                if( action == WSConstants.SIGN ){
                    actionName = "Signature"
                }else if( action == WSConstants.TS ){
                    actionName = "Timestamp"
                }else if( action == WSConstants.BST ){
                    actionName = "Binary Security Token"
                }
                Boolean validatedToken = result.get(WSSecurityEngineResult.TAG_VALIDATED_TOKEN);
                logger.info("Result[$actionName => $validatedToken]: ");
                result.keySet().each{ key ->
                    String value = result.get(key)?.toString() ?: "";
                    value = value.replaceAll("\n", "");
                    if( value?.trim()?.length() > 20 )
                        value = value?.trim()?.substring(0, 20) + "...";
                    logger.info("\t [$key] => ${value}");
                }
            }
        }

        logger.debug("Finished evaluating WSS4j WS-Security!")
    }//end evaluate()


}//end WSS4jSecurityPolicyRule