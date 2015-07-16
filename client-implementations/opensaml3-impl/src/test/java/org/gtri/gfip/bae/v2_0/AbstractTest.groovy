package org.gtri.gfip.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.junit.After
import org.junit.Before
import org.opensaml.core.config.InitializationService

/**
 * Created by brad on 7/16/15.
 */
abstract class AbstractTest {
    static Logger logger = LoggerFactory.get(AbstractTest)

    static {
        // According to https://wiki.shibboleth.net/confluence/display/OS30/Initialization+and+Configuration
        //   we initialize Open SAML 3 once and only once.
        logger.debug("Initializing opensaml 3...");
        InitializationService.initialize();
    }



    @Before
    public void startTest(){
        logger.info("============================== STARTING TEST ==============================")
    }
    @After
    public void stopTest(){
        logger.info("============================== STOPPING TEST ==============================\n\n")
    }


}
