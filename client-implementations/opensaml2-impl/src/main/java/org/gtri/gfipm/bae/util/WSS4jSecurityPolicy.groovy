package org.gtri.gfipm.bae.util;

import gtri.logging.Logger;
import gtri.logging.LoggerFactory
import org.opensaml.ws.message.MessageContext;
import org.opensaml.ws.security.SecurityPolicy
import org.opensaml.ws.security.SecurityPolicyException
import org.opensaml.ws.security.SecurityPolicyRule

import java.security.PrivateKey
import java.security.cert.X509Certificate;

/**
 * A {@link SecurityPolicy} which verifies web service security headers on incoming messages.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/04/24 12:56 PM
 */
public class WSS4jSecurityPolicy implements SecurityPolicy {
    //==================================================================================================================
    //  Static Variables
    //==================================================================================================================
    static Logger logger = LoggerFactory.get(WSS4jSecurityPolicy)
    //==================================================================================================================
    //  Constructors
    //==================================================================================================================
    public WSS4jSecurityPolicy(Boolean force, PrivateKey clientPrivateKey, X509Certificate clientCertificate, Collection<X509Certificate> serverCertificates){
        rules.add( new WSS4jSecurityPolicyRule(force, clientPrivateKey, clientCertificate, serverCertificates) )
    }
    //==================================================================================================================
    //  Instance Variables
    //==================================================================================================================
    private List<WSS4jSecurityPolicyRule> rules = []
    //==================================================================================================================
    //  Interface Implementations
    //==================================================================================================================
    @Override
    List<SecurityPolicyRule> getPolicyRules() {
        logger.debug("Returning rules...")
        return rules
    }

    @Override
    void evaluate(MessageContext messageContext) throws SecurityPolicyException {
        logger.debug("Evaluating @|cyan ${this.rules?.size()}|@ rules...")
        this.rules?.each{ rule ->
            rule.evaluate(messageContext)
        }
    }//end evaluate()




}//end WSS4jSecurityPolicy