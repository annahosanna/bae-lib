package org.gtri.gfipm.bae.v2_0

import org.bouncycastle.jce.provider.X509CertificateObject
import org.bouncycastle.openssl.PEMReader
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class TestPIVUUIDSubjectIdentifier extends AbstractTest {

    static String CERT_FILE_NAME = "piv16.crt"

    @Test
    public void testHasPIVUUIDFromCertificate() throws Exception {
        logger.info("Testing that an X509 certificate which contains a PIVI UUID can be parsed...")

        logger.debug("Reading cert...")
        File certFile = getCertFile(CERT_FILE_NAME);
        X509CertificateObject cert = readCert(certFile);
        assertThat(cert, notNullValue());

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("piv-16-test"))

        logger.debug("Asserting presence of valid PIVUUID...")
        assertThat(PIVUUIDSubjectIdentifier.hasPIVIUUID(cert), equalTo(Boolean.TRUE))

        logger.debug("Reading subject identifier from cert...")
        PIVUUIDSubjectIdentifier subjectId = new PIVUUIDSubjectIdentifier(cert);
        assertThat(subjectId.getName(), equalTo("urn:uuid:048051b4-2288-41fd-b895-5fe9945e1c63"))

        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()")
    }//end testHasPIVUUIDFromCertificate()


}//end TestFASCNSubjectIdentifier