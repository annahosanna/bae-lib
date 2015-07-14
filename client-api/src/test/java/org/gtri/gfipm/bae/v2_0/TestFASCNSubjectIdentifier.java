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


    public static String FASCN_CERT_FILE_NAME = "fascn-cert-card1.crt";

    @Test
    public void testHasFASCNFromCertificate() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N can be parsed...");

        logger.debug("Reading cert...");
        File certFile = getCertFile(FASCN_CERT_FILE_NAME);
        X509CertificateObject cert = readCert(certFile);

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("Test Cardholder"));
        assertThat(cert.getSubjectDN().toString(), containsString("Test Agency"));
        assertThat(cert.getSubjectDN().toString(), containsString("Test Department"));
        assertThat(cert.getSubjectDN().toString(), containsString("Test Government"));

        logger.debug("Asserting presence of valid FASCN...");
        assertThat(FASCNSubjectIdentifier.hasFASCN(cert), equalTo(Boolean.TRUE));

        logger.debug("Reading subject identifier from cert...");
        FASCNSubjectIdentifier subjectId = new FASCNSubjectIdentifier(cert);

        assertThat(subjectId.getAgencyCode(), equalTo(3201));
        assertThat(subjectId.getSystemCode(), equalTo(295));
        assertThat(subjectId.getCredentialNumber(), equalTo(759494));
        assertThat(subjectId.getCredentialSeries(), equalTo(1));
        assertThat(subjectId.getIndividualCredentialIssue(), equalTo(1));
        assertThat(subjectId.getPersonIdentifier(), equalTo(6464979587l));
        assertThat(subjectId.getOrganizationalCategory(), equalTo(1));
        assertThat(subjectId.getOrganizationalIdentifier(), equalTo(3201));
        assertThat(subjectId.getPersonOrganizationAssociationCategory(), equalTo(1));


        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()");
    }//end testHasFASCNFromCertificate()




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






    public static String FASCN_CERT_FILE3_NAME = "piv3.crt";

    @Test
    public void testHasFASCNFromCertificate3() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N (3) can be parsed...");

        logger.debug("Reading cert...");
        File certFile = getCertFile(FASCN_CERT_FILE3_NAME);
        X509CertificateObject cert = readCert(certFile);

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




    public static String FASCN_CERT_FILE4_NAME = "piv4.crt";

    @Test
    public void testHasFASCNFromCertificate4() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N (4) can be parsed...");

        logger.debug("Reading cert...");
        File certFile = getCertFile(FASCN_CERT_FILE4_NAME);
        X509CertificateObject cert = readCert(certFile);

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("piv-4-test"));

        logger.debug("Asserting presence of valid FASCN...");
        assertThat(FASCNSubjectIdentifier.hasFASCN(cert), equalTo(Boolean.TRUE));

        logger.debug("Reading subject identifier from cert...");
        FASCNSubjectIdentifier subjectId = new FASCNSubjectIdentifier(cert);

        assertThat(subjectId.getAgencyCode(), equalTo(3201));
        assertThat(subjectId.getSystemCode(), equalTo(3733));
        assertThat(subjectId.getCredentialNumber(), equalTo(334893));
        assertThat(subjectId.getCredentialSeries(), equalTo(1));
        assertThat(subjectId.getIndividualCredentialIssue(), equalTo(3));
        assertThat(subjectId.getPersonIdentifier(), equalTo(1152472674l));
        assertThat(subjectId.getOrganizationalCategory(), equalTo(1));
        assertThat(subjectId.getOrganizationalIdentifier(), equalTo(3201));
        assertThat(subjectId.getPersonOrganizationAssociationCategory(), equalTo(1));


        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()");
    }//end testHasFASCNFromCertificate()



    public static String FASCN_CERT_FILE5_NAME = "piv5.crt";

    @Test
    public void testHasFASCNFromCertificate5() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N (5) can be parsed...");

        logger.debug("Reading cert...");
        File certFile = getCertFile(FASCN_CERT_FILE5_NAME);
        X509CertificateObject cert = readCert(certFile);

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("piv-5-test"));

        logger.debug("Asserting presence of valid FASCN...");
        assertThat(FASCNSubjectIdentifier.hasFASCN(cert), equalTo(Boolean.TRUE));

        logger.debug("Reading subject identifier from cert...");
        FASCNSubjectIdentifier subjectId = new FASCNSubjectIdentifier(cert);

        assertThat(subjectId.getAgencyCode(), equalTo(3201));
        assertThat(subjectId.getSystemCode(), equalTo(1922));
        assertThat(subjectId.getCredentialNumber(), equalTo(843789));
        assertThat(subjectId.getCredentialSeries(), equalTo(2));
        assertThat(subjectId.getIndividualCredentialIssue(), equalTo(3));
        assertThat(subjectId.getPersonIdentifier(), equalTo(4110207347l));
        assertThat(subjectId.getOrganizationalCategory(), equalTo(1));
        assertThat(subjectId.getOrganizationalIdentifier(), equalTo(3201));
        assertThat(subjectId.getPersonOrganizationAssociationCategory(), equalTo(1));


        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()");
    }//end testHasFASCNFromCertificate()


    static String FASCN_CERT_FILE6_NAME = "piv6.crt";

    @Test
    public void testHasFASCNFromCertificate6() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N (6) can be parsed...");

        logger.debug("Reading cert...");
        File certFile = getCertFile(FASCN_CERT_FILE6_NAME);
        X509CertificateObject cert = readCert(certFile);

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("piv-6-test"));

        logger.debug("Asserting presence of valid FASCN...");
        assertThat(FASCNSubjectIdentifier.hasFASCN(cert), equalTo(Boolean.TRUE));

        logger.debug("Reading subject identifier from cert...");
        FASCNSubjectIdentifier subjectId = new FASCNSubjectIdentifier(cert);

        assertThat(subjectId.getAgencyCode(), equalTo(3201));
        assertThat(subjectId.getSystemCode(), equalTo(889));
        assertThat(subjectId.getCredentialNumber(), equalTo(895303));
        assertThat(subjectId.getCredentialSeries(), equalTo(1));
        assertThat(subjectId.getIndividualCredentialIssue(), equalTo(1));
        assertThat(subjectId.getPersonIdentifier(), equalTo(4340730641l));
        assertThat(subjectId.getOrganizationalCategory(), equalTo(1));
        assertThat(subjectId.getOrganizationalIdentifier(), equalTo(3201));
        assertThat(subjectId.getPersonOrganizationAssociationCategory(), equalTo(5));


        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()");
    }//end testHasFASCNFromCertificate()

    static String FASCN_CERT_FILE7_NAME = "piv7.crt";

    @Test
    public void testHasFASCNFromCertificate7() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N (7) can be parsed...");

        logger.debug("Reading cert...");
        File certFile = getCertFile(FASCN_CERT_FILE7_NAME);
        X509CertificateObject cert = readCert(certFile);

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("piv-7-test"));

        logger.debug("Asserting presence of valid FASCN...");
        assertThat(FASCNSubjectIdentifier.hasFASCN(cert), equalTo(Boolean.TRUE));

        logger.debug("Reading subject identifier from cert...");
        FASCNSubjectIdentifier subjectId = new FASCNSubjectIdentifier(cert);

        assertThat(subjectId.getAgencyCode(), equalTo(3201));
        assertThat(subjectId.getSystemCode(), equalTo(1719));
        assertThat(subjectId.getCredentialNumber(), equalTo(265));
        assertThat(subjectId.getCredentialSeries(), equalTo(1));
        assertThat(subjectId.getIndividualCredentialIssue(), equalTo(6));
        assertThat(subjectId.getPersonIdentifier(), equalTo(7514170617l));
        assertThat(subjectId.getOrganizationalCategory(), equalTo(1));
        assertThat(subjectId.getOrganizationalIdentifier(), equalTo(3201));
        assertThat(subjectId.getPersonOrganizationAssociationCategory(), equalTo(1));


        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()");
    }//end testHasFASCNFromCertificate()


    static String FASCN_CERT_FILE9_NAME = "piv9.crt";

    @Test
    public void testHasFASCNFromCertificate9() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N (9) can be parsed...");

        logger.debug("Reading cert...");
        File certFile = getCertFile(FASCN_CERT_FILE9_NAME);
        X509CertificateObject cert = readCert(certFile);

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("piv-9-test"));

        logger.debug("Asserting presence of valid FASCN...");
        assertThat(FASCNSubjectIdentifier.hasFASCN(cert), equalTo(Boolean.TRUE));

        logger.debug("Reading subject identifier from cert...");
        FASCNSubjectIdentifier subjectId = new FASCNSubjectIdentifier(cert);

        assertThat(subjectId.getAgencyCode(), equalTo(3201));
        assertThat(subjectId.getSystemCode(), equalTo(5424));
        assertThat(subjectId.getCredentialNumber(), equalTo(119949));
        assertThat(subjectId.getCredentialSeries(), equalTo(1));
        assertThat(subjectId.getIndividualCredentialIssue(), equalTo(1));
        assertThat(subjectId.getPersonIdentifier(), equalTo(2243747282l));
        assertThat(subjectId.getOrganizationalCategory(), equalTo(1));
        assertThat(subjectId.getOrganizationalIdentifier(), equalTo(3201));
        assertThat(subjectId.getPersonOrganizationAssociationCategory(), equalTo(5));


        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()");
    }//end testHasFASCNFromCertificate()


    static String FASCN_CERT_FILE12_NAME = "piv12.crt";

    @Test
    public void testHasFASCNFromCertificate12() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N (12) can be parsed...");

        logger.debug("Reading cert...");
        File certFile = getCertFile(FASCN_CERT_FILE12_NAME);
        X509CertificateObject cert = readCert(certFile);

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("piv-12-test"));

        logger.debug("Asserting presence of valid FASCN...");
        assertThat(FASCNSubjectIdentifier.hasFASCN(cert), equalTo(Boolean.TRUE));

        logger.debug("Reading subject identifier from cert...");
        FASCNSubjectIdentifier subjectId = new FASCNSubjectIdentifier(cert);

        assertThat(subjectId.getAgencyCode(), equalTo(3201));
        assertThat(subjectId.getSystemCode(), equalTo(5167));
        assertThat(subjectId.getCredentialNumber(), equalTo(114200));
        assertThat(subjectId.getCredentialSeries(), equalTo(1));
        assertThat(subjectId.getIndividualCredentialIssue(), equalTo(1));
        assertThat(subjectId.getPersonIdentifier(), equalTo(4001354205l));
        assertThat(subjectId.getOrganizationalCategory(), equalTo(1));
        assertThat(subjectId.getOrganizationalIdentifier(), equalTo(3201));
        assertThat(subjectId.getPersonOrganizationAssociationCategory(), equalTo(1));


        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()");
    }//end testHasFASCNFromCertificate()


    static String FASCN_CERT_FILE13_NAME = "piv13.crt";

    @Test
    public void testHasFASCNFromCertificate13() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N (13) can be parsed...");

        logger.debug("Reading cert...");
        File certFile = getCertFile(FASCN_CERT_FILE13_NAME);
        X509CertificateObject cert = readCert(certFile);

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("piv-13-test"));

        logger.debug("Asserting presence of valid FASCN...");
        assertThat(FASCNSubjectIdentifier.hasFASCN(cert), equalTo(Boolean.TRUE));

        logger.debug("Reading subject identifier from cert...");
        FASCNSubjectIdentifier subjectId = new FASCNSubjectIdentifier(cert);

        assertThat(subjectId.getAgencyCode(), equalTo(3201));
        assertThat(subjectId.getSystemCode(), equalTo(2096));
        assertThat(subjectId.getCredentialNumber(), equalTo(177465));
        assertThat(subjectId.getCredentialSeries(), equalTo(1));
        assertThat(subjectId.getIndividualCredentialIssue(), equalTo(1));
        assertThat(subjectId.getPersonIdentifier(), equalTo(8949244215l));
        assertThat(subjectId.getOrganizationalCategory(), equalTo(1));
        assertThat(subjectId.getOrganizationalIdentifier(), equalTo(3201));
        assertThat(subjectId.getPersonOrganizationAssociationCategory(), equalTo(1));


        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()");
    }//end testHasFASCNFromCertificate()


    static String FASCN_CERT_FILE16_NAME = "piv16.crt";

    @Test
    public void testHasFASCNFromCertificate16() throws Exception {
        logger.info("Testing that an X509 certificate which contains a FASC-N (16) can be parsed...");

        logger.debug("Reading cert...");
        File certFile = getCertFile(FASCN_CERT_FILE16_NAME);
        X509CertificateObject cert = readCert(certFile);

        logger.debug("Subject DN: "+cert.getSubjectDN().toString());
        assertThat(cert.getSubjectDN().toString(), containsString("piv-16-test"));

        logger.debug("Asserting lack of presence of valid FASCN...");
        assertThat(FASCNSubjectIdentifier.hasFASCN(cert), equalTo(Boolean.FALSE));


        logger.info("Successfully tested the hasFASCN() method on FASCNSubjectIdentifier()");
    }//end testHasFASCNFromCertificate()




}//end TestFASCNSubjectIdentifier