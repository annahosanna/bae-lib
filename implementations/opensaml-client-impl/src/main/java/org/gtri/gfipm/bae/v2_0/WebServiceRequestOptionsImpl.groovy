package org.gtri.gfipm.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory

/**
 * Simple implementation of the {@link WebServiceRequestOptions} interface.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/05/02 9:59 AM
 */
class WebServiceRequestOptionsImpl implements WebServiceRequestOptions {

    static Logger logger = LoggerFactory.get(WebServiceRequestOptionsImpl.class)

    // Underlying storage
    Properties properties

    public WebServiceRequestOptionsImpl() {
        properties = new Properties()
    }
    // TODO File constructor? etc.




    @Override
    Boolean getBoolean(String key) {
        String value = this.getString(key);
        if( value != null )
            try{
                Boolean bool = Boolean.parseBoolean(value)
                return bool;
            }catch(Throwable t){
                throw new UnsupportedOperationException("Cannot parse value[${value}] of key[${key}] into a boolean.", t);
            }
        throw new UnsupportedOperationException("Could not find value for key[$key]")
    }

    @Override
    Boolean getBoolean(String key, Boolean defaultValue) {
        try{
            Boolean value = this.getBoolean(key)
            return value != null ? value : defaultValue
        }catch(Throwable t){
            logger.debug("Error getting field '@|red $key|@' from web service request options: "+t);
            return defaultValue;
        }
    }

    @Override
    Number getNumber(String key) {
        String value = this.getString(key);
        if( value != null )
            try{
                Number num = Double.parseDouble(value) // TODO is Double the most all-encompassing value?
                return num;
            }catch(Throwable t){
                throw new UnsupportedOperationException("Cannot parse value[${value}] of key[${key}] into a number.", t);
            }
    }

    @Override
    Number getNumber(String key, Number defaultValue) {
        try{
            Number num = this.getNumber(key)
            return num ? num : defaultValue
        }catch(Throwable t){
            logger.error("Error getting field '@|red $key|@' from web service request options: "+t);
            return defaultValue;
        }
    }

    @Override
    String getString(String key) {
        return properties.getProperty(key);
    }

    @Override
    String getString(String key, String defaultValue) {
        String property = getString(key);
        property ? property : defaultValue
    }

    @Override
    void debugPrint() {
        logger.info("WebServiceRequestOptions: ")
        this.properties?.keySet()?.each{ key ->
            logger.info("    [@|cyan $key|@] = [@|green ${this.properties.getProperty(key)}|@]")
        }
    }//end


}
