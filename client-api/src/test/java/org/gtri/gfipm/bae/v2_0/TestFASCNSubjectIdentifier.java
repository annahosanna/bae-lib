package org.gtri.gfipm.bae.v2_0;

import org.bouncycastle.jce.provider.X509CertificateObject;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class TestFASCNSubjectIdentifier extends AbstractTest {


    @Test
    public void testValidFASCN1() throws Exception {
        logger.debug("Testing a valid FASC-N can be parsed...");

        String fascn = "70001234000005119000000001170005";
        FASCNSubjectIdentifier id = new FASCNSubjectIdentifier(fascn);
        assertThat(id, notNullValue());
        assertThat(id.getName(), equalTo(fascn));

        assertThat(id.getAgencyCode(), equalTo(7000));
        assertThat(id.getSystemCode(), equalTo(1234));
        assertThat(id.getCredentialNumber(), equalTo(5));
        assertThat(id.getCredentialSeries(), equalTo(1));
        assertThat(id.getIndividualCredentialIssue(), equalTo(1));
        assertThat(id.getPersonIdentifier(), equalTo(9000000001l));
        assertThat(id.getOrganizationalCategory(), equalTo(1));
        assertThat(id.getOrganizationalIdentifier(), equalTo(7000));
        assertThat(id.getPersonOrganizationAssociationCategory(), equalTo(5));

        logger.info("Successfully parsed a valid FASC-N!");
    }//end testValidFASCN1()


    public static String FASCN_CERT_FILE2_NAME = "./src/test/resources/certs/piv2.crt";

    @Test
    public void testHasFASCNFromCertificate2() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N (2) can be parsed...");

        logger.debug("Reading cert...");
        X509CertificateObject cert = readCert(FASCN_CERT_FILE2_NAME);

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("piv-2-test"));

        logger.debug("Asserting presence of valid FASCN...");
        assertThat(FASCNSubjectIdentifier.hasFASCN(cert), equalTo(Boolean.TRUE));

        logger.debug("Reading subject identifier from cert...");
        FASCNSubjectIdentifier subjectId = new FASCNSubjectIdentifier(cert);

        assertThat(subjectId.getAgencyCode(), equalTo(3201));
        assertThat(subjectId.getSystemCode(), equalTo(295));
        assertThat(subjectId.getCredentialNumber(), equalTo(723474));
        assertThat(subjectId.getCredentialSeries(), equalTo(1));
        assertThat(subjectId.getIndividualCredentialIssue(), equalTo(1));
        assertThat(subjectId.getPersonIdentifier(), equalTo(9614127727l));
        assertThat(subjectId.getOrganizationalCategory(), equalTo(1));
        assertThat(subjectId.getOrganizationalIdentifier(), equalTo(3201));
        assertThat(subjectId.getPersonOrganizationAssociationCategory(), equalTo(5));


        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()");
    }//end testHasFASCNFromCertificate()






    public static String FASCN_CERT_FILE3_NAME = "./src/test/resources/certs/piv3.crt";

    @Test
    public void testHasFASCNFromCertificate3() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N (3) can be parsed...");

        logger.debug("Reading cert...");
        X509CertificateObject cert = readCert(FASCN_CERT_FILE3_NAME);

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("piv-3-test"));

        logger.debug("Asserting presence of valid FASCN...");
        assertThat(FASCNSubjectIdentifier.hasFASCN(cert), equalTo(Boolean.TRUE));

        logger.debug("Reading subject identifier from cert...");
        FASCNSubjectIdentifier subjectId = new FASCNSubjectIdentifier(cert);

        assertThat(subjectId.getAgencyCode(), equalTo(3201));
        assertThat(subjectId.getSystemCode(), equalTo(8575));
        assertThat(subjectId.getCredentialNumber(), equalTo(714931));
        assertThat(subjectId.getCredentialSeries(), equalTo(1));
        assertThat(subjectId.getIndividualCredentialIssue(), equalTo(2));
        assertThat(subjectId.getPersonIdentifier(), equalTo(7163720148l));
        assertThat(subjectId.getOrganizationalCategory(), equalTo(1));
        assertThat(subjectId.getOrganizationalIdentifier(), equalTo(3201));
        assertThat(subjectId.getPersonOrganizationAssociationCategory(), equalTo(1));


        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()");
    }//end testHasFASCNFromCertificate()



    static String FASCN_CERT_FILE16_NAME = "./src/test/resources/certs/piv16.crt";

    @Test
    public void testHasFASCNFromCertificate16() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N (16) can be parsed...");

        logger.debug("Reading cert...");
        X509CertificateObject cert = readCert(FASCN_CERT_FILE16_NAME);

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("piv-16-test"));

        logger.debug("Asserting lack of presence of valid FASCN...");
        assertThat(FASCNSubjectIdentifier.hasFASCN(cert), equalTo(Boolean.FALSE));


        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()");
    }//end testHasFASCNFromCertificate()




}//end TestFASCNSubjectIdentifier