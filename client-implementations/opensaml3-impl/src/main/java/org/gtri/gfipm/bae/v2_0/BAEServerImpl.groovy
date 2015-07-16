package org.gtri.gfipm.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory

class BAEServerImpl implements BAEServer {

    static Logger logger = LoggerFactory.get(BAEServerImpl)

    BAEClientInfo clientInfo
    BAEServerInfo serverInfo
    WebServiceRequestOptions webServiceRequestOptions

    private Boolean shouldUseClientCertInTLS() {
        if( webServiceRequestOptions )
            return webServiceRequestOptions.getBoolean(WebServiceRequestOptions.CLIENT_CERT_AUTH, WebServiceRequestOptions.CLIENT_CERT_AUTH_DEFAULT);
        else
            return WebServiceRequestOptions.CLIENT_CERT_AUTH_DEFAULT
    }

    private String getStringValue(String name, String defaultValue) {
        if( webServiceRequestOptions ){
            return webServiceRequestOptions.getString(name, defaultValue);
        }else{
            return defaultValue;
        }
    }

    @Override
    Collection<BackendAttribute> attributeQuery(SubjectIdentifier subjectId) throws BAEServerException {
        logger.info("Request to do attributeQuery on @|cyan ${subjectId}|@...")

        throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
    }//end attributeQuery()



}/* end BAEServerImpl */