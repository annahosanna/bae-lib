package org.gtri.gfipm.bae.v2_0

import org.gtri.gfipm.bae.AbstractTest
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue

class TestBAEServerInfoFactoryImpl extends AbstractTest {


    @Test
    public void testFound(){
        def instance = BAEServerInfoFactory.getInstance()
        assertThat(instance, notNullValue())
        assertThat(instance instanceof BAEServerInfoFactoryImpl, is(true))
    }

}
