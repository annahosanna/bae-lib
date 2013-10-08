package org.gtri.gfipm.bae.v2_0

import org.gtri.gfipm.bae.AbstractTest
import org.junit.Test
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

/**
 * Created with IntelliJ IDEA.
 * User: slee35
 * Date: 10/8/13
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
class TestWebServiceRequestOptionsImpl extends AbstractTest {

    @Test
    public void testBooleans() {
        logger.info("Testing that booleans work in WebServiceRequestOptions as expected...")

        logger.debug("Testing when values are set...")
        def options = [:]
        options.put(WebServiceRequestOptions.CLIENT_CERT_AUTH, "false")
        options.put(WebServiceRequestOptions.SERVER_CERT_AUTH, "true")
        WebServiceRequestOptions wsRequestOptions = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(options);
        assertThat(wsRequestOptions, notNullValue())
        wsRequestOptions.debugPrint()
        assertThat(wsRequestOptions.getBoolean(WebServiceRequestOptions.CLIENT_CERT_AUTH), equalTo(Boolean.FALSE))
        assertThat(wsRequestOptions.getBoolean(WebServiceRequestOptions.SERVER_CERT_AUTH), equalTo(Boolean.TRUE))
        wsRequestOptions = null;

        logger.debug("Testing when have values and given defaults...")
        options = [:]
        options.put(WebServiceRequestOptions.CLIENT_CERT_AUTH, "true")
        options.put(WebServiceRequestOptions.SERVER_CERT_AUTH, "false")
        wsRequestOptions = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(options);
        assertThat(wsRequestOptions, notNullValue())
        wsRequestOptions.debugPrint()
        assertThat(wsRequestOptions.getBoolean(WebServiceRequestOptions.CLIENT_CERT_AUTH, Boolean.FALSE), equalTo(Boolean.TRUE))
        assertThat(wsRequestOptions.getBoolean(WebServiceRequestOptions.SERVER_CERT_AUTH, Boolean.TRUE), equalTo(Boolean.FALSE))
        wsRequestOptions = null;

        logger.debug("Testing when values are defaults...")
        options = [:]
        wsRequestOptions = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(options);
        assertThat(wsRequestOptions, notNullValue())
        wsRequestOptions.debugPrint()
        assertThat(wsRequestOptions.getBoolean(WebServiceRequestOptions.CLIENT_CERT_AUTH, WebServiceRequestOptions.CLIENT_CERT_AUTH_DEFAULT), equalTo(WebServiceRequestOptions.CLIENT_CERT_AUTH_DEFAULT))
        assertThat(wsRequestOptions.getBoolean(WebServiceRequestOptions.SERVER_CERT_AUTH, WebServiceRequestOptions.SERVER_CERT_AUTH_DEFAULT), equalTo(WebServiceRequestOptions.SERVER_CERT_AUTH_DEFAULT))


        logger.info("Successfully Tested that booleans work in WebServiceRequestOptions as expected!")
    }//end testBooleans()

    @Test
    public void testNumbers() {
        logger.info("Testing that numbers work in WebServiceRequestOptions as expected...")

        logger.debug("Testing when values are set...")
        def options = [:]
        options.put("a", "1")
        WebServiceRequestOptions wsRequestOptions = WebServiceRequestOptionsFactory.getInstance().createWebServiceRequestOptions(options);
        assertThat(wsRequestOptions, notNullValue())
        assertThat(wsRequestOptions.getNumber("a"), equalTo(new Double(1.0d)))
        wsRequestOptions = null;

        logger.info("Successfully Tested that numbers work in WebServiceRequestOptions as expected!")
    }//end testBooleans()

}
