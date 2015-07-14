package org.gtri.gfipm.bae.v2_0

import org.gtri.gfipm.bae.AbstractTest
import org.junit.Test
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

class TestBAEClientInfoFactoryImpl extends AbstractTest {


    @Test
    public void testFound(){
        def instance = BAEClientInfoFactory.getInstance()
        assertThat(instance, notNullValue())
        assertThat(instance instanceof BAEClientInfoFactoryImpl, is(true))
    }

}
