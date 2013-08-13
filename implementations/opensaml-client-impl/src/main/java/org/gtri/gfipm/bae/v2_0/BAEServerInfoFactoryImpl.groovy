package org.gtri.gfipm.bae.v2_0

import java.security.cert.X509Certificate

class BAEServerInfoFactoryImpl extends BAEServerInfoFactory {

    @Override
    BAEServerInfo createBAEServerInfo(String endpointUrl, String destination, List<X509Certificate> certificates) {
        BAEServerInfoImpl impl = new BAEServerInfoImpl()

        impl.endpointAddress = endpointUrl
        impl.destination = destination
        impl.certificates = certificates

        return impl
    }


}
