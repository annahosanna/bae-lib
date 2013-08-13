package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory

import javax.net.ssl.X509TrustManager
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

/**
 * Created with IntelliJ IDEA.
 * User: brad
 * Date: 5/28/13
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
class DirectX509TrustManager implements X509TrustManager {
    static final Logger logger = LoggerFactory.get(DirectX509TrustManager)

    Collection<X509Certificate> serverCertificates
    XMLCryptoHelperServer serverCrypto
    public DirectX509TrustManager(Collection<X509Certificate> serverCertificates){
        this.serverCertificates = serverCertificates;
        this.serverCrypto = new XMLCryptoHelperServer(serverCertificates)
    }

    @Override
    void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        logger.info("Checking if client trusted: [chain: $chain], authtype: $authType")
    }

    @Override
    void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        logger.debug("Checking if server trusted...")
        try{
            serverCrypto.verifyTrust(chain, false)
        }catch(Throwable t){
            logger.error("Error verifying trust!", t);
            throw new CertificateException(t.getMessage(), t);
        }
    }

    @Override
    X509Certificate[] getAcceptedIssuers() {
        logger.info("Returning all accepted issuers...")
        return serverCertificates as X509Certificate[];
    }


    //==================================================================================================================
    //  Helper Methods
    //==================================================================================================================
    private Boolean containsCert(X509Certificate cert){
        boolean foundCert = Boolean.FALSE
        this.serverCertificates?.each{ serverCert ->
            logger.debug("Comparing @|cyan ${serverCert}|@ to @|green ${cert}|@...")
            if( serverCert.equals(cert) ){
                logger.debug("Found matching certificate[@|blue ${serverCert.getSubjectDN()}|@]!")
                foundCert = Boolean.TRUE;
            }else if( cert.getIssuerDN().equals(serverCert.getSubjectDN()) ){
                logger.debug("Found issuer DN match: @|cyan ${serverCert.subjectDN}|@")
                foundCert = Boolean.TRUE
            }
        }
        return foundCert;
    }

}//end DirectX509TrustManager()