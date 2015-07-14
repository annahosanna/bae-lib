package org.gtri.gfipm.bae.v2_0;

import gtri.logging.Logger;
import gtri.logging.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Allows for creating an instance of {@link WebServiceRequestOptions} easily.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 5/28/2013 1:00 PM
 */
public abstract class WebServiceRequestOptionsFactory {


    private static final Logger logger = LoggerFactory.get(WebServiceRequestOptionsFactory.class);
    private static WebServiceRequestOptionsFactory instance;

    public static WebServiceRequestOptionsFactory getInstance() {
        if( instance != null )
            return instance;

        logger.info("Loading WebServiceRequestOptionsFactory using ServiceLoader...");
        ServiceLoader<WebServiceRequestOptionsFactory> serviceLoader = ServiceLoader.load(WebServiceRequestOptionsFactory.class);
        Iterator<WebServiceRequestOptionsFactory> serviceFactoryIterator = serviceLoader.iterator();
        while( serviceFactoryIterator.hasNext() ){
            WebServiceRequestOptionsFactory next = serviceFactoryIterator.next();
            if( instance == null ){
                logger.debug("@|green Selecting|@ WebServiceRequestOptionsFactory based on class @|cyan %s|@...", next.getClass().getName());
                instance = next;
            }else{
                logger.debug("@|red Not|@ selecting WebServiceRequestOptionsFactory based on class @|warn %s|@...", next.getClass().getName());
            }
        }

        return instance;
    }//end getInstance()




    //==================================================================================================================
    //  Abstract Service Methods
    //==================================================================================================================

    /**
     * Creates an instanceof {@link WebServiceRequestOptions} based on the incoming {@link Map}.
     */
    public abstract WebServiceRequestOptions createWebServiceRequestOptions(Map<String, String> properties);


}//end WebServiceRequestOptionsFactory