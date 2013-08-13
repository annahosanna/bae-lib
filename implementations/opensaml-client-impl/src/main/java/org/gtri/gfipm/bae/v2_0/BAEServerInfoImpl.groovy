package org.gtri.gfipm.bae.v2_0

import java.security.cert.X509Certificate

class BAEServerInfoImpl implements BAEServerInfo {

    String endpointAddress
    String destination
    List<X509Certificate> certificates = []

}
