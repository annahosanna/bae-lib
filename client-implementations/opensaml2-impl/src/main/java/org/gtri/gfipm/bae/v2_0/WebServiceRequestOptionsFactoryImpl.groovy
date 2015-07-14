package org.gtri.gfipm.bae.v2_0

/**
 * Simple implementation of the {@link WebServiceRequestOptionsFactory} class.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 5/28/2013 1:03 PM
 */
class WebServiceRequestOptionsFactoryImpl extends WebServiceRequestOptionsFactory {

    @Override
    WebServiceRequestOptions createWebServiceRequestOptions(Map<String, String> properties) {
        WebServiceRequestOptionsImpl impl = new WebServiceRequestOptionsImpl()
        properties.keySet().each{ property ->
            // TODO Check here for invalid property?
            impl.getProperties().setProperty(property, properties.get(property));
        }
        return impl;
    }

}//end WebServiceRequestOptionsFactoryImpl