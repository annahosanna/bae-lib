package org.gtri.gfipm.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory

class BAEServerFactoryImpl extends BAEServerFactory {

    static Logger logger = LoggerFactory.get(BAEServerFactoryImpl)

    @Override
    BAEServer createBAEServer(BAEServerInfo serverInfo, BAEClientInfo clientInfo) throws BAEServerCreationException {
        BAEServerImpl serverImpl = new BAEServerImpl()
        serverImpl.clientInfo = clientInfo
        serverImpl.serverInfo = serverInfo
        return serverImpl
    }

    @Override
    BAEServer createBAEServer(BAEServerInfo serverInfo, BAEClientInfo clientInfo, WebServiceRequestOptions opts) throws BAEServerCreationException {
        BAEServerImpl serverImpl = new BAEServerImpl()
        serverImpl.clientInfo = clientInfo
        serverImpl.serverInfo = serverInfo
        serverImpl.webServiceRequestOptions = opts
        return serverImpl
    }
}
