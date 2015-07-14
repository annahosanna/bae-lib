package org.gtri.gfipm.bae.v2_0

import java.security.PrivateKey
import java.security.cert.X509Certificate

class BAEClientInfoImpl implements BAEClientInfo {


    String identifier
    X509Certificate certificate
    PrivateKey privateKey


}
