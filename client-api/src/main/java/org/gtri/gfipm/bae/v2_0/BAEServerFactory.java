package org.gtri.gfipm.bae.v2_0;

import gtri.logging.Logger;
import gtri.logging.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * A factory-pattern class for creating instances of {@link BAEServer}.
 * <br/><br/>
 * User: brad
 * Date: 4/6/13 8:56 AM
 */
public abstract class BAEServerFactory {

    private static final Logger logger = LoggerFactory.get(BAEServerFactory.class);
    private static BAEServerFactory instance;

    public static BAEServerFactory getInstance() {
        if( instance != null )
            return instance;

        logger.info("Loading BAEServerFactory using ServiceLoader...");
        ServiceLoader<BAEServerFactory> serviceLoader = ServiceLoader.load(BAEServerFactory.class);
        Iterator<BAEServerFactory> serviceFactoryIterator = serviceLoader.iterator();
        while( serviceFactoryIterator.hasNext() ){
            BAEServerFactory next = serviceFactoryIterator.next();
            if( instance == null ){
                logger.debug("@|green Selecting|@ BAEServerFactory based on class @|cyan %s|@...", next.getClass().getName());
                instance = next;
            }else{
                logger.debug("@|red Not|@ selecting BAEServerFactory based on class @|warn %s|@...", next.getClass().getName());
            }
        }

        return instance;
    }//end getInstance()



    //==================================================================================================================
    //  Abstract Service Methods
    //==================================================================================================================

    public abstract BAEServer createBAEServer( BAEServerInfo serverInfo, BAEClientInfo clientInfo ) throws BAEServerCreationException;

    public abstract BAEServer createBAEServer( BAEServerInfo serverInfo, BAEClientInfo clientInfo, WebServiceRequestOptions opts ) throws BAEServerCreationException;

    // TODO Figure out what other configuration options are necessary and set those.

}//end BAEServerFactory