package org.gtri.gfipm.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import net.shibboleth.utilities.java.support.xml.SerializeSupport
import org.gtri.gfipm.bae.v2_0.BAEClientInfo
import org.opensaml.core.config.ConfigurationService
import org.opensaml.core.xml.XMLObjectBuilderFactory
import org.opensaml.core.xml.config.XMLObjectProviderRegistry
import org.opensaml.core.xml.io.Marshaller
import org.opensaml.saml.saml2.core.AttributeQuery
import org.opensaml.security.credential.Credential
import org.opensaml.security.credential.CredentialSupport
import org.opensaml.xmlsec.signature.Signature
import org.opensaml.xmlsec.signature.support.DocumentInternalIDContentReference
import org.opensaml.xmlsec.signature.support.SignatureConstants
import org.opensaml.xmlsec.signature.support.Signer
import org.w3c.dom.Element

import java.security.KeyPair
import java.security.PrivateKey
import java.security.cert.X509Certificate

/**
 * Knows how to create digital signatures of {@link AttributeQuery} objects.
 * <br/><br/>
 * Created by brad on 7/17/15.
 */
class AttributeQuerySigner {

    static Logger logger = LoggerFactory.get(AttributeQuerySigner.class)

    public static Signature sign(AttributeQuery query, KeyPair keyPair){
        Credential credential = CredentialSupport.getSimpleCredential(keyPair.getPublic(), keyPair.getPrivate());
        return sign(query, credential);
    }

    public static Signature sign(AttributeQuery query, X509Certificate publicKey, PrivateKey privateKey){
        Credential credential = CredentialSupport.getSimpleCredential(publicKey, privateKey);
        return sign(query, credential);
    }

    public static Signature sign(AttributeQuery query, BAEClientInfo clientInfo){
        if( clientInfo == null )
            throw new NullPointerException("BAEClientInfo is null, and must have a value in order to sign the AttributeQuery.")
        Credential credential = CredentialSupport.getSimpleCredential(clientInfo.getCertificate(), clientInfo.getPrivateKey());
        return sign(query, credential);
    }//end sign()

    /**
     * Performs the signing of this attribute query using the Open SAML 3 libraries.  Code example from:
     *     http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-xmlsec-impl/src/test/java/org/opensaml/xmlsec/signature/support/EnvelopedSignatureTest.java?view=markup
     * <br/><br/>
     * @param query
     */
    public static Signature sign(AttributeQuery query, Credential credential){
        if( query == null )
            throw new UnsupportedOperationException("AttributeQuery is required to have a non-null value in order to compute a signature")
        if( credential == null )
            throw new UnsupportedOperationException("Credential is required to have a non-null value in order to compute a signature")

        logger.debug("Request to digitally sign AttributeQuery[id=@|cyan ${query.getID()}|@]");

        logger.debug("Building Signature Object...");
        XMLObjectProviderRegistry xmlObjectProviderRegistry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        XMLObjectBuilderFactory xmlObjectBuilderFactory = xmlObjectProviderRegistry?.getBuilderFactory();
        if( xmlObjectBuilderFactory == null )
            throw new NullPointerException("Unable to obtain instance of '${XMLObjectBuilderFactory.class.name}' from the OpenSAML ConfigurationService.")

        Signature signature = xmlObjectBuilderFactory.getBuilder(Signature.DEFAULT_ELEMENT_NAME).buildObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        signature.setSigningCredential(credential);

        DocumentInternalIDContentReference contentReference = new DocumentInternalIDContentReference(query.getID());
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_ENVELOPED_SIGNATURE);
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        contentReference.setDigestAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA1);
        signature.getContentReferences().add(contentReference);

        query.setSignature(signature);

        logger.debug("Performing marshalling...");
        Marshaller marshaller = xmlObjectProviderRegistry.getMarshallerFactory().getMarshaller(query);
        Element signedElement = marshaller.marshall(query);

        logger.debug("Signing...");
        Signer.signObject(signature);

        logger.info("Successfully signed ${AttributeQuery.class.getSimpleName()}[id=@|cyan ${query.getID()}|@]: \n"+SerializeSupport.prettyPrintXML(signedElement));
        return signature;
    }//end sign()


}