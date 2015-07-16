package org.gtri.gfipm.bae.v2_0

import java.security.PrivateKey
import java.security.cert.X509Certificate

class BAEClientInfoFactoryImpl extends BAEClientInfoFactory {

    @Override
    BAEClientInfo createBAEClientInfo(String identifier, X509Certificate certificate, PrivateKey privateKey) {
        BAEClientInfoImpl impl = new BAEClientInfoImpl()

        impl.identifier = identifier
        impl.certificate = certificate
        impl.privateKey = privateKey

        return impl
    }

}//end BAEClientInfoFactoryImpl