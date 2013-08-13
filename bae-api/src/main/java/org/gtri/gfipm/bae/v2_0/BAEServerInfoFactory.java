package org.gtri.gfipm.bae.v2_0;

import gtri.logging.Logger;
import gtri.logging.LoggerFactory;

import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * BAEServerInfoFactory is useful for creating instances of {@link BAEServerInfo}.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/05/01 8:47 PM
 */
public abstract class BAEServerInfoFactory {

    private static final Logger logger = LoggerFactory.get(BAEClientInfoFactory.class);
    private static BAEServerInfoFactory instance;

    public static BAEServerInfoFactory getInstance() {
        if( instance != null )
            return instance;

        logger.info("Loading BAEServerInfoFactory using ServiceLoader...");
        ServiceLoader<BAEServerInfoFactory> serviceLoader = ServiceLoader.load(BAEServerInfoFactory.class);
        Iterator<BAEServerInfoFactory> serviceFactoryIterator = serviceLoader.iterator();
        while( serviceFactoryIterator.hasNext() ){
            BAEServerInfoFactory next = serviceFactoryIterator.next();
            if( instance == null ){
                logger.debug("@|green Selecting|@ BAEServerInfoFactory based on class @|cyan %s|@...", next.getClass().getName());
                instance = next;
            }else{
                logger.debug("@|red Not|@ selecting BAEServerInfoFactory based on class @|warn %s|@...", next.getClass().getName());
            }
        }

        return instance;
    }//end getInstance()


    //==================================================================================================================
    //  Abstract Service Methods
    //==================================================================================================================
    public abstract BAEServerInfo createBAEServerInfo(String endpointUrl, String destination, List<X509Certificate> certificates);


}//end BAEServerInfoFactory