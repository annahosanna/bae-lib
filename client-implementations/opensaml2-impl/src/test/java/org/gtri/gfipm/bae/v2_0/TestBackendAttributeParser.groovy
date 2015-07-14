package org.gtri.gfipm.bae.v2_0

import org.gtri.gfipm.bae.AbstractTest
import org.junit.Test
import org.w3c.dom.Document

import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

class TestBackendAttributeParser extends AbstractTest {


    static String IIR_ATTRIBUTE_1 =
"""<Attribute xmlns="urn:oasis:names:tc:SAML:2.0:assertion" Name="gfipm:2.0:user:28CFRCertificationIndicator" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:basic">
    <AttributeValue xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xsd:string">false</AttributeValue>
</Attribute>
"""

    @Test
    public void testIIRAttributeValueCase1() {
        logger.info("Testing IIR attribute value parsing case (with 'false')...")

        List<BackendAttributeValue> values = BackendAttributeParser.parseValues(IIR_ATTRIBUTE_1);
        assertThat(values, notNullValue())
        assertThat(values.size(), equalTo(1))

        BackendAttributeValue value = values.get(0);
        assertThat(value, notNullValue())
        assertThat(value.getStringValue(), equalTo("false"))
        assertThat(value.getBooleanValue(), equalTo(Boolean.FALSE))

        logger.info("Successfully parsed IIR attribute value!")
    }//end testIIRAttributeValueCase()




    static String IIR_ATTRIBUTE_2 =
"""<Attribute xmlns="urn:oasis:names:tc:SAML:2.0:assertion" Name="gfipm:2.0:user:28CFRCertificationIndicator" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:basic">
    <AttributeValue xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xsd:int">1</AttributeValue>
</Attribute>
"""

    @Test
    public void testIIRAttributeValueCase2() {
        logger.info("Testing IIR attribute value parsing case (with '1')...")

        List<BackendAttributeValue> values = BackendAttributeParser.parseValues(IIR_ATTRIBUTE_2);
        assertThat(values, notNullValue())
        assertThat(values.size(), equalTo(1))

        BackendAttributeValue value = values.get(0);
        assertThat(value, notNullValue())
        assertThat(value.getStringValue(), equalTo("1"))
        assertThat(value.getBooleanValue(), equalTo(Boolean.TRUE))

        logger.info("Successfully parsed IIR attribute value!")
    }//end testIIRAttributeValueCase()








    static String XSD_ANY_CASE =
        """<Attribute xmlns="urn:oasis:names:tc:SAML:2.0:assertion" Name="gfipm:2.0:user:28CFRCertificationIndicator" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:basic">
    <AttributeValue>
        <custom:custom-value xmlns:custom="urn:org:gtri:custom:1.0">this is markup</custom:custom-value>
    </AttributeValue>
</Attribute>
"""

    @Test
    public void testXsdAnyCase() {
        logger.info("Testing xsd any case...")

        List<BackendAttributeValue> values = BackendAttributeParser.parseValues(XSD_ANY_CASE);
        assertThat(values, notNullValue())
        assertThat(values.size(), equalTo(1))

        BackendAttributeValue value = values.get(0);
        assertThat(value, notNullValue())
        assertThat(value.getStringValue(), containsString("custom-value"))
        assertThat(value.getStringValue(), containsString("AttributeValue"))
        assertThat(value.getStringValue(), containsString("this is markup"))

        Document doc = value.getXmlValue()
        assertThat(doc, notNullValue())
        assertThat(doc.getDocumentElement().getTagName(), containsString("AttributeValue"))

        logger.info("Successfully parsed xsd any case!")
    }//end testIIRAttributeValueCase()






    static String NUMERIC_CASE =
        """<Attribute xmlns="urn:oasis:names:tc:SAML:2.0:assertion" Name="gfipm:2.0:user:28CFRCertificationIndicator" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:basic">
    <AttributeValue xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xsd:double">2.0</AttributeValue>
</Attribute>
"""

    @Test
    public void testNumericCase() {
        logger.info("Testing numeric case...")

        List<BackendAttributeValue> values = BackendAttributeParser.parseValues(NUMERIC_CASE);
        assertThat(values, notNullValue())
        assertThat(values.size(), equalTo(1))

        BackendAttributeValue value = values.get(0);
        assertThat(value, notNullValue())
        assertThat(value.getStringValue(), equalTo("2.0"))
        assertThat(value.getNumericValue(), equalTo(2.0d))

        logger.info("Successfully parsed numeric case!")
    }//end testNumericCase()


}//end TestBackendAttributeParser