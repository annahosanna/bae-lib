package org.gtri.gfip.bae.util

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.gtri.gfip.bae.AbstractTest
import org.gtri.gfipm.bae.util.AttributeQueryBuilder
import org.gtri.gfipm.bae.v2_0.EmailSubjectIdentifier
import org.gtri.gfipm.bae.v2_0.FASCNSubjectIdentifier
import org.gtri.gfipm.bae.v2_0.PIVUUIDSubjectIdentifier
import org.gtri.gfipm.bae.v2_0.SubjectIdentifier
import org.junit.Test
import org.opensaml.saml.saml2.core.AttributeQuery
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

/**
 * Created by brad on 7/17/15.
 */
class TestAttributeQueryBuilder extends AbstractTest {

    static Logger logger = LoggerFactory.get(TestAttributeQueryBuilder.class);

    static String ID_VALUE = "048051b4-2288-41fd-b895-5fe9945e1c63";

    @Test
    public void testBuildPivUUID() {
        logger.info("Testing the ability to build a simple attribute query based on PIV UUID...")
        SubjectIdentifier sid = new PIVUUIDSubjectIdentifier(ID_VALUE)
        AttributeQuery attributeQuery = AttributeQueryBuilder.build(sid, "test1", "test-destination-id", "test-issuer-id");
        assertThat(attributeQuery, notNullValue())
        assertThat(attributeQuery.getDestination(), equalTo("test-destination-id"))
        assertThat(attributeQuery.getID(), equalTo("test1"))
        assertThat(attributeQuery.getIssuer(), notNullValue())
        assertThat(attributeQuery.getIssuer().getValue(), equalTo("test-issuer-id"))
        assertThat(attributeQuery.getSubject(), notNullValue())
        assertThat(attributeQuery.getSubject().getNameID(), notNullValue())
        assertThat(attributeQuery.getSubject().getNameID().getFormat(), equalTo(PIVUUIDSubjectIdentifier.FORMAT))
        assertThat(attributeQuery.getSubject().getNameID().getValue(), equalTo(ID_VALUE))

        logger.info("Can build a simple PIV UUID AttributeQuery object!");
    }//end testBuildSimple()


    @Test
    public void testBuildFASCN() {
        logger.info("Testing the ability to build a simple attribute query based on FASC-N...")
        SubjectIdentifier sid = new FASCNSubjectIdentifier(1, 1, 1, 1, 1, 1l, 1, 1, 1);
        AttributeQuery attributeQuery = AttributeQueryBuilder.build(sid, "test2", "test-destination-id", "test-issuer-id");
        assertThat(attributeQuery, notNullValue())
        assertThat(attributeQuery.getDestination(), equalTo("test-destination-id"))
        assertThat(attributeQuery.getID(), equalTo("test2"))
        assertThat(attributeQuery.getIssuer(), notNullValue())
        assertThat(attributeQuery.getIssuer().getValue(), equalTo("test-issuer-id"))
        assertThat(attributeQuery.getSubject(), notNullValue())
        assertThat(attributeQuery.getSubject().getNameID(), notNullValue())
        assertThat(attributeQuery.getSubject().getNameID().getFormat(), equalTo(FASCNSubjectIdentifier.FASCN_FORMAT))
        assertThat(attributeQuery.getSubject().getNameID().getValue(), equalTo("00010001000001110000000001100011"))

        logger.info("Can build a simple FASC-N AttributeQuery object!");
    }//end testBuildSimple()

    @Test
    public void testBuildEmail() {
        logger.info("Testing the ability to build a simple attribute query based on EmailIdentifier...")
        SubjectIdentifier sid = new EmailSubjectIdentifier("test@example.org");
        AttributeQuery attributeQuery = AttributeQueryBuilder.build(sid, "test3", "test-destination-id", "test-issuer-id");
        assertThat(attributeQuery, notNullValue())
        assertThat(attributeQuery.getDestination(), equalTo("test-destination-id"))
        assertThat(attributeQuery.getID(), equalTo("test3"))
        assertThat(attributeQuery.getIssuer(), notNullValue())
        assertThat(attributeQuery.getIssuer().getValue(), equalTo("test-issuer-id"))
        assertThat(attributeQuery.getSubject(), notNullValue())
        assertThat(attributeQuery.getSubject().getNameID(), notNullValue())
        assertThat(attributeQuery.getSubject().getNameID().getFormat(), equalTo(EmailSubjectIdentifier.EMAIL_FORMAT))
        assertThat(attributeQuery.getSubject().getNameID().getValue(), equalTo("test@example.org"))

        logger.info("Can build a simple Email AttributeQuery object!");
    }//end testBuildSimple()

}//end TestAttributeQueryBuilder