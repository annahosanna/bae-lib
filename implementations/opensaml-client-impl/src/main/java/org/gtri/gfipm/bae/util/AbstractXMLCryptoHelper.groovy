package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.apache.ws.security.WSSecurityException
import org.apache.ws.security.components.crypto.Crypto
import org.apache.ws.security.components.crypto.CryptoType
import org.apache.ws.security.components.crypto.DERDecoder
import org.apache.ws.security.components.crypto.Merlin
import org.apache.ws.security.components.crypto.X509SubjectPublicKeyInfo
import org.apache.ws.security.util.WSSecurityUtil

import javax.security.auth.callback.CallbackHandler
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

/**
 * This class helps to bridge the gap between SAML Crypto and WSS4J Crypto.
 * <br/><br/>
 * User: brad
 * Date: 2013/4/19 2:11 PM
 */
abstract class AbstractXMLCryptoHelper implements Crypto {

    static Logger logger = LoggerFactory.get(AbstractXMLCryptoHelper)

    private String cryptoProvider;

    String getCryptoProvider(){
        return cryptoProvider
    }

    void setCryptoProvider(String provider){
        logger.info("setCryptoProvider('@|green ${provider}|@')")
        this.cryptoProvider = provider
    }


    /**
     * Reads the SubjectKeyIdentifier information from the certificate.
     * <p/>
     * If the the certificate does not contain a SKI extension then
     * try to compute the SKI according to RFC3280 using the
     * SHA-1 hash value of the public key. The second method described
     * in RFC3280 is not support. Also only RSA public keys are supported.
     * If we cannot compute the SKI throw a WSSecurityException.
     *
     * @param cert The certificate to read SKI
     * @return The byte array containing the binary SKI data
     */
    public static final String SKI_OID = "2.5.29.14";
    byte[] getSKIBytesFromCert(X509Certificate cert) throws WSSecurityException{
        // Gets the DER-encoded OCTET string for the extension value (extnValue)
        // identified by the passed-in oid String. The oid string is represented
        // by a set of positive whole numbers separated by periods.
        //
        byte[] derEncodedValue = cert.getExtensionValue(SKI_OID);

        if (cert.getVersion() < 3 || derEncodedValue == null) {
            X509SubjectPublicKeyInfo spki = new X509SubjectPublicKeyInfo(cert.getPublicKey());
            byte[] value = spki.getSubjectPublicKey();
            try {
                return WSSecurityUtil.generateDigest(value);
            } catch (WSSecurityException ex) {
                throw new WSSecurityException(
                        WSSecurityException.UNSUPPORTED_SECURITY_TOKEN, "noSKIHandling",
                        ["No SKI certificate extension and no SHA1 message digest available"] as Object[],
                        ex
                );
            }
        }

        //
        // Strip away first (four) bytes from the DerValue (tag and length of
        // ExtensionValue OCTET STRING and KeyIdentifier OCTET STRING)
        //
        DERDecoder extVal = new DERDecoder(derEncodedValue);
        extVal.expect(DERDecoder.TYPE_OCTET_STRING);  // ExtensionValue OCTET STRING
        extVal.getLength();
        extVal.expect(DERDecoder.TYPE_OCTET_STRING);  // KeyIdentifier OCTET STRING
        int keyIDLen = extVal.getLength();
        return extVal.getBytes(keyIDLen);
    }


    /**
     * Get the CertificateFactory instance on this Crypto instance
     *
     * @return Returns a <code>CertificateFactory</code> to construct
     *         X509 certificates
     * @throws org.apache.ws.security.WSSecurityException
     */
    CertificateFactory getCertificateFactory() throws WSSecurityException{
        // This is a vast understatement of complexity here, to make things work.  @see Merlin.getCertificateFactory()
        return CertificateFactory.getInstance("X.509");
    }

    //
    // Base Crypto functionality methods
    //

    /**
     * Load a X509Certificate from the input stream.
     *
     * @param in The <code>InputStream</code> containing the X509 data
     * @return An X509 certificate
     * @throws WSSecurityException
     */
    X509Certificate loadCertificate(InputStream inStream) throws WSSecurityException{
        try {
            CertificateFactory certFactory = getCertificateFactory();
            return (X509Certificate) certFactory.generateCertificate(inStream);
        } catch (CertificateException e) {
            throw new WSSecurityException(
                    WSSecurityException.SECURITY_TOKEN_UNAVAILABLE, "parseError", null, e
            );
        }
    }

    //==================================================================================================================
    //  Default implementations which are designed to throw errors, alerting you to the fact you need to implement them.
    //==================================================================================================================
    String getX509Identifier(X509Certificate cert) throws WSSecurityException {
        logger.error("NOT YET IMPLEMENTED: getX509Identifier()")
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED")
    }

    PrivateKey getPrivateKey(String identifier, String password) throws WSSecurityException{
        logger.error("NOT YET IMPLEMENTED: getPrivateKey(identifier=$identifier, password=***)")
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED: getPrivateKey(identifier=$identifier, password=***)")
    }

    PrivateKey getPrivateKey(X509Certificate certificate, CallbackHandler callbackHandler) throws WSSecurityException {
        logger.error("NOT YET IMPLEMENTED: getPrivateKey(cert, callbackHandler)")
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED")
    }

    boolean verifyTrust( X509Certificate[] certs, boolean enableRevocation ) throws WSSecurityException{
        logger.debug("Verifying trust of the following certificate chain:")
        certs?.each{ cert ->
            logger.debug("\t Cert Principle: @|cyan ${cert.subjectDN}|@")
        }


        logger.error("NOT YET IMPLEMENTED: verifyTrust( X509Certificate[] certs, boolean enableRevocation )")
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED: verifyTrust( X509Certificate[] certs, boolean enableRevocation )")
    }

    boolean verifyTrust(X509Certificate[] certs) throws WSSecurityException {
        logger.debug("Verifying trust of the following certificate chain:")
        certs?.each{ cert ->
            logger.debug("\t Cert Principle: @|cyan ${cert.subjectDN}|@")
        }

        logger.error("NOT YET IMPLEMENTED: verifyTrust(X509Certificate[] certs)")
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED")
    }

    boolean verifyTrust(PublicKey publicKey) throws WSSecurityException{
        logger.debug("Verifying trust of public key: $publicKey")

        logger.error("NOT YET IMPLEMENTED: verifyTrust(PublicKey publicKey)")
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED")
    }

    byte[] getBytesFromCertificates(X509Certificate[] certs) throws WSSecurityException{
        logger.error("NOT YET IMPLEMENTED: getBytesFromCertificates()")
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED")
    }

    X509Certificate[] getCertificatesFromBytes(byte[] data) throws WSSecurityException{
        logger.error("NOT YET IMPLEMENTED: getCertificatesFromBytes()")
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED")
    }

    String getDefaultX509Identifier() throws WSSecurityException{
        logger.error("NOT YET IMPLEMENTED: getDefaultX509Identifier()")
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED")
    }

    void setDefaultX509Identifier(String identifier){
        logger.error("NOT YET IMPLEMENTED: setDefaultX509Identifier('${identifier}')")
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED")
    }

    void setCertificateFactory(String provider, CertificateFactory certFactory){
        logger.error("NOT YET IMPLEMENTED: setCertificateFactory()")
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED")
    }


}
