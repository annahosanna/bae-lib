package org.gtri.gfipm.bae.util

import org.gtri.gfipm.bae.AbstractTest
import org.gtri.gfipm.bae.util.AttributeQueryBuilder
import org.gtri.gfipm.bae.util.AttributeQuerySigner
import org.gtri.gfipm.bae.v2_0.EmailSubjectIdentifier
import org.junit.Assert
import org.junit.Test
import org.opensaml.saml.saml2.core.AttributeQuery
import org.opensaml.security.credential.Credential
import org.opensaml.security.credential.CredentialSupport
import org.opensaml.security.crypto.KeySupport
import org.opensaml.xmlsec.signature.Signature
import org.opensaml.xmlsec.signature.support.SignatureValidator
import java.security.cert.X509Certificate
import org.opensaml.security.x509.X509Support


import java.security.KeyPair
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

/**
 * Created by brad on 7/17/15.
 */
class TestAttributeQuerySigner extends AbstractTest {

    static String TEST_CERT = "gtri-pilot-iir.crt";

    /**
     * Generates a key pair and a simple AttributeQuery and then signs it.  Asserts that the credential validates it and
     * another does not.
     */
    @Test
    public void testSignSimple() {
        logger.info("Signing an attribute query...");

        KeyPair keyPair = KeySupport.generateKeyPair("RSA", 1024, null);
        assertThat(keyPair, notNullValue())
        assertThat(keyPair.getPrivate(), notNullValue())
        assertThat(keyPair.getPublic(), notNullValue())

        Credential credential = CredentialSupport.getSimpleCredential(keyPair.getPublic(), keyPair.getPrivate());
        assertThat(credential, notNullValue());

        AttributeQuery query = AttributeQueryBuilder.build(new EmailSubjectIdentifier("test@example.org"), "sign-test1", "destination1", "issuer1");
        assertThat(query, notNullValue())

        File clientCertFile = getCertFile(TEST_CERT)
        X509Certificate clientCert = X509Support.decodeCertificate(clientCertFile)


        Signature signature = AttributeQuerySigner.sign(query, credential, clientCert);
        assertThat(signature, notNullValue());

        logger.debug("Validating signature...");
        SignatureValidator.validate(signature, credential);

        KeyPair keyPair2 = KeySupport.generateKeyPair("RSA", 1024, null);
        Credential credential2 = CredentialSupport.getSimpleCredential(keyPair2.getPublic(), keyPair2.getPrivate());
        try{
            SignatureValidator.validate(signature, credential2);
            Assert.fail("We were able to validate the signature with a different keypair, this should not be possible.")
        }catch(Throwable t){
            logger.debug("Successfully tested that another keypair does not validate our signature: "+t.toString())
        }

        logger.info("Successfully signed an AttributeQuery!");
    }//end testSignSimple()

}//end TestAttributeQuerySigner
