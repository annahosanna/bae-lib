package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.apache.ws.security.WSSecurityException
import org.apache.ws.security.components.crypto.CryptoType
import org.gtri.gfipm.bae.v2_0.WebServiceRequestOptions
import org.gtri.gfipm.bae.v2_0.WebServiceRequestOptionsImpl

import java.security.cert.CertPath
import java.security.cert.CertPathValidator
import java.security.cert.PKIXParameters
import java.security.cert.TrustAnchor
import java.security.cert.X509Certificate

/**
 * This class helps to bridge the gap between SAML Crypto and WSS4J Crypto. Only handles methods necessary for the
 * server certificate negotiation.
 * <br/><br/>
 * User: brad
 * Date: 2013/4/19 2:11 PM
 */
class XMLCryptoHelperServer extends AbstractXMLCryptoHelper {

    static Logger logger = LoggerFactory.get(XMLCryptoHelperServer)

    Collection<X509Certificate> serverCertificates
    Boolean shouldValidateServerCert;
    public XMLCryptoHelperServer(Collection<X509Certificate> serverCertificates){
        this.serverCertificates = serverCertificates
        this.shouldValidateServerCert = WebServiceRequestOptions.SERVER_CERT_AUTH_DEFAULT
    }
    public XMLCryptoHelperServer(Collection<X509Certificate> serverCertificates, WebServiceRequestOptions opts){
        this.serverCertificates = serverCertificates
        this.shouldValidateServerCert = opts?.getBoolean(WebServiceRequestOptions.SERVER_CERT_AUTH, WebServiceRequestOptions.SERVER_CERT_AUTH_DEFAULT) ?: WebServiceRequestOptions.SERVER_CERT_AUTH_DEFAULT;
        opts?.debugPrint();
        logger.info("Value of shouldValidateServerCert: @|yellow ${shouldValidateServerCert}|@")
    }


    /**
     * Get an X509Certificate (chain) corresponding to the CryptoType argument. The supported
     * types are as follows:
     *
     * TYPE.ISSUER_SERIAL - A certificate (chain) is located by the issuer name and serial number
     * TYPE.THUMBPRINT_SHA1 - A certificate (chain) is located by the SHA1 of the (root) cert
     * TYPE.SKI_BYTES - A certificate (chain) is located by the SKI bytes of the (root) cert
     * TYPE.SUBJECT_DN - A certificate (chain) is located by the Subject DN of the (root) cert
     * TYPE.ALIAS - A certificate (chain) is located by an alias. This alias is implementation
     * specific, for example - it could be a java KeyStore alias.
     */
    X509Certificate[] getX509Certificates(CryptoType cryptoType) throws WSSecurityException{
        logger.info("Returning certificates for cryptoType: ${cryptoType}")
        def certs = []
        if( this.getServerCertificates() && !this.getServerCertificates().isEmpty())
            certs.addAll(this.getServerCertificates())
        return certs as X509Certificate[];
    }

    /**
     * OID For the NameConstraints Extension to X.509
     *
     * http://java.sun.com/j2se/1.4.2/docs/api/
     * http://www.ietf.org/rfc/rfc3280.txt (s. 4.2.1.11)
     */
    public static final String NAME_CONSTRAINTS_OID = "2.5.29.30";

    boolean isDirectlyTrusted(List<X509Certificate> certList){
        Map<X509Certificate, Boolean> certsFound = [:]
        certList.each{ givenCert ->
            certsFound.put(givenCert, Boolean.FALSE)
            serverCertificates.each{ serverCert ->
                if( givenCert.equals(serverCert) )
                    certsFound.put(givenCert, Boolean.TRUE)
            }
        }
        boolean allFound = true;
        for( X509Certificate cert : certList ){
            if( certsFound.get(cert) == Boolean.FALSE ) {
                allFound = false;
                break;
            }
        }
        return allFound;
    }

    @Override
    boolean verifyTrust(X509Certificate[] certs, boolean enableRevocation) throws WSSecurityException {
        try{
            logger.info("Verifying trust of certificates:")
            certs?.each{ cert ->
                logger.info("    Cert: @|cyan ${cert.subjectDN}|@")
            }
            if( !this.shouldValidateServerCert ){
                logger.warn("Skipping authentication on server certificates!")
                return true;
            }
            logger.debug("Setting trust anchors: ")
            Set<TrustAnchor> set = new HashSet<TrustAnchor>();
            serverCertificates.each{ X509Certificate cert ->
                logger.debug("   Trust Anchor: @|green ${cert.subjectDN}|@")
                TrustAnchor anchor = new TrustAnchor(cert, cert.getExtensionValue(NAME_CONSTRAINTS_OID));
                set.add(anchor);
            }

            // Generate cert path
            List<X509Certificate> certList = certs as List;
            if( isDirectlyTrusted(certList) ){
                logger.info("We have directly found each certificate, returning @|green TRUSTED|@...")
                return true;
            }

            CertPath path = getCertificateFactory().generateCertPath(certList);

            PKIXParameters param = new PKIXParameters(set);
            param.setRevocationEnabled(enableRevocation);
    //        if (enableRevocation && crlCertStore != null) {
    //            param.addCertStore(crlCertStore);
    //        }

            logger.debug("Resolving CertPathValidator...")
            // Verify the trust path using the above settings
            String provider = getCryptoProvider();
            CertPathValidator validator = null;
            if (provider == null || provider.length() == 0) {
                validator = CertPathValidator.getInstance("PKIX");
            } else {
                validator = CertPathValidator.getInstance("PKIX", provider);
            }
            logger.debug("Validating with CertPathValidator(@|blue ${validator?.getClass().getName()}|@)...")
            validator.validate(path, param);

            logger.info("Certificates are @|green TRUSTED|@!")
            return true;
        } catch (java.security.NoSuchProviderException e) {
            throw new WSSecurityException(
                    WSSecurityException.FAILURE, "certpath",
                    [e.getMessage() ] as Object[], e
            );
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new WSSecurityException(
                    WSSecurityException.FAILURE,
                    "certpath", [e.getMessage()] as Object[],
                    e
            );
        } catch (java.security.cert.CertificateException e) {
            throw new WSSecurityException(
                    WSSecurityException.FAILURE, "certpath",
                    [e.getMessage()] as Object[], e
            );
        } catch (java.security.InvalidAlgorithmParameterException e) {
            throw new WSSecurityException(
                    WSSecurityException.FAILURE, "certpath",
                    [e.getMessage()] as Object[], e
            );
        } catch (java.security.cert.CertPathValidatorException e) {
            logger.error("Error validating certificate path.", e);
            throw new WSSecurityException(
                    WSSecurityException.FAILURE, "certpath",
                    [e.getMessage()] as Object[], e
            );
        } catch (java.security.KeyStoreException e) {
            throw new WSSecurityException(
                    WSSecurityException.FAILURE, "certpath",
                    [e.getMessage()] as Object[], e
            );
        } catch (NullPointerException e) {
            // NPE thrown by JDK 1.7 for one of the test cases
            throw new WSSecurityException(
                    WSSecurityException.FAILURE, "certpath",
                    [e.getMessage()] as Object[], e
            );
        }
    }//end verifyTrust()


}//end class XMLCryptoHelperServer
