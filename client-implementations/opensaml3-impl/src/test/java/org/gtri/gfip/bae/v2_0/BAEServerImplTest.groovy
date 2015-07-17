package org.gtri.gfip.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.gtri.gfip.bae.AbstractTest
import org.gtri.gfipm.bae.v2_0.BAEServerImpl
import org.gtri.gfipm.bae.v2_0.PIVUUIDSubjectIdentifier
import org.junit.Test

/**
 * Created by brad on 7/16/15.
 */
class BAEServerImplTest extends AbstractTest {

    static Logger logger = LoggerFactory.get(BAEServerImplTest.class);

    static String ID_VALUE = "048051b4-2288-41fd-b895-5fe9945e1c63";

    @Test
    public void testIt(){
        logger.info("Testing BAE Server Impl...")

        BAEServerImpl baeServer = new BAEServerImpl();

        baeServer.attributeQuery(new PIVUUIDSubjectIdentifier(ID_VALUE));

    }//end testIt()

}
