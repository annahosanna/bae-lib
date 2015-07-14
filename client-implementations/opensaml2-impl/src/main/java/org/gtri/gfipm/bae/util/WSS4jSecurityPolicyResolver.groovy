package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.opensaml.ws.message.MessageContext
import org.opensaml.ws.security.SecurityPolicy
import org.opensaml.ws.security.SecurityPolicyResolver

import java.security.PrivateKey
import java.security.cert.X509Certificate

/**
 * This class will provide SecurityPolicy enforcement to SAML for WS Security headers.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/04/24 8:27 AM
 */
class WSS4jSecurityPolicyResolver implements SecurityPolicyResolver {
    //==================================================================================================================
    //  Static Variables
    //==================================================================================================================
    static Logger logger = LoggerFactory.get(WSS4jSecurityPolicyResolver)
    //==================================================================================================================
    //  Constructors
    //==================================================================================================================
    public WSS4jSecurityPolicyResolver(PrivateKey newPrivateKey, X509Certificate clientCertificate, Collection<X509Certificate> serverCertificates){
        this.privateKey = newPrivateKey
        this.clientCertificate = clientCertificate
        this.serverCertificates = serverCertificates
    }
    //==================================================================================================================
    //  Instance Variables
    //==================================================================================================================
    PrivateKey privateKey
    X509Certificate clientCertificate
    Collection<X509Certificate> serverCertificates
    Boolean force = Boolean.TRUE
    //==================================================================================================================
    //  Interface Implementations
    //==================================================================================================================

    @Override
    Iterable<SecurityPolicy> resolve(MessageContext criteria) throws org.opensaml.xml.security.SecurityException {
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED.  This class only implements resolveSingle() since HttpSOAPClient only calls that.")
    }

    @Override
    SecurityPolicy resolveSingle(MessageContext criteria) throws org.opensaml.xml.security.SecurityException {
        logger.debug("Resolving WSS4jSecurityPolicy...")
        return new WSS4jSecurityPolicy(this.getForce(), this.getPrivateKey(), this.getClientCertificate(), this.getServerCertificates())
    }//end resolveSingle()

}//end WSSecurityPolicyResolver