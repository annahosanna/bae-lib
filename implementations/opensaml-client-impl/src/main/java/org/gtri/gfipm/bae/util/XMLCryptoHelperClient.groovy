package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.apache.ws.security.WSSecurityException
import org.apache.ws.security.components.crypto.Crypto
import org.apache.ws.security.components.crypto.CryptoType
import org.apache.ws.security.components.crypto.DERDecoder
import org.apache.ws.security.components.crypto.X509SubjectPublicKeyInfo
import org.apache.ws.security.util.WSSecurityUtil

import javax.security.auth.callback.CallbackHandler
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

/**
 * This class helps to bridge the gap between SAML Crypto and WSS4J Crypto.  Only handles methods necessary for the
 * client certificate/private key negotiation.
 * <br/><br/>
 * User: brad
 * Date: 2013/4/19 2:11 PM
 */
class XMLCryptoHelperClient extends AbstractXMLCryptoHelper {

    static Logger logger = LoggerFactory.get(XMLCryptoHelperClient)

    PrivateKey privateKey
    X509Certificate clientCertificate
    public XMLCryptoHelperClient(PrivateKey newPrivateKey, X509Certificate clientCertificate){
        this.privateKey = newPrivateKey
        this.clientCertificate = clientCertificate
    }

    X509Certificate[] getX509Certificates(CryptoType cryptoType) throws WSSecurityException{
        logger.info("Returning certificates for cryptoType: ${cryptoType}")
        def certs = []
        certs.add(this.clientCertificate)
        return certs as X509Certificate[];
    }

//    boolean verifyTrust( X509Certificate[] certs, boolean enableRevocation ) throws WSSecurityException{
//        logger.debug("Verifying trust of the following certificate chain:")
//        certs?.each{ cert ->
//            logger.debug("\t Cert Principle: @|cyan ${cert.subjectDN}|@")
//        }
//
//        logger.debug("Against the following local certificate: @|green ${this.getClientCertificate().subjectDN}|@")
//        Boolean foundClientCert = false
//        for( int i = certs.length - 1; i >= 0; i-- ){
//            X509Certificate cert = certs[i]
//            if( !foundClientCert && cert.equals(this.getClientCertificate()) ){
//                foundClientCert = true;
//            }
//        }
//
//        return foundClientCert
//    }

    /**
     * Gets the private key corresponding to the identifier.
     *
     * @param identifier The implementation-specific identifier corresponding to the key
     * @param password The password needed to get the key
     * @return The private key
     */
    PrivateKey getPrivateKey(String identifier, String password) throws WSSecurityException{
        logger.debug("Returning private key...")
        return this.getPrivateKey()
    }

}//end XMLCryptoHelperClient