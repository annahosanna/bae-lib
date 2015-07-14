package org.gtri.gfipm.bae.v2_0;

import gtri.logging.Logger;
import gtri.logging.LoggerFactory;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Useful for creating instances of the {@link BAEClientInfo} interface.
 * <br/><br/>
 * User: brad
 * Date: 2013/05/01 8:38 PM
 */
public abstract class BAEClientInfoFactory {


    private static final Logger logger = LoggerFactory.get(BAEClientInfoFactory.class);
    private static BAEClientInfoFactory instance;

    public static BAEClientInfoFactory getInstance() {
        if( instance != null )
            return instance;

        logger.info("Loading BAEClientInfoFactory using ServiceLoader...");
        ServiceLoader<BAEClientInfoFactory> serviceLoader = ServiceLoader.load(BAEClientInfoFactory.class);
        Iterator<BAEClientInfoFactory> serviceFactoryIterator = serviceLoader.iterator();
        while( serviceFactoryIterator.hasNext() ){
            BAEClientInfoFactory next = serviceFactoryIterator.next();
            if( instance == null ){
                logger.debug("@|green Selecting|@ BAEClientInfoFactory based on class @|cyan %s|@...", next.getClass().getName());
                instance = next;
            }else{
                logger.debug("@|red Not|@ selecting BAEClientInfoFactory based on class @|warn %s|@...", next.getClass().getName());
            }
        }

        return instance;
    }//end getInstance()




    //==================================================================================================================
    //  Abstract Service Methods
    //==================================================================================================================
    public abstract BAEClientInfo createBAEClientInfo(
        String issuerIdentifier,
        X509Certificate certificate,
        PrivateKey privateKey
    );


}//end BAEClientInfoFactory