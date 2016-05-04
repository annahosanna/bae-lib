package org.gtri.gfipm.bae

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.junit.After
import org.junit.Before
import org.opensaml.core.config.InitializationService

import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*


/**
 * Created by brad on 7/16/15.
 */
abstract class AbstractTest {
    static Logger logger = LoggerFactory.get(AbstractTest)

    protected Boolean dirContainsChildren( File directory, String ... children ){
        List<String> actualChildren = []
        directory.listFiles().each{ file ->
            actualChildren.add(file.name)
        }
        boolean allFound = true
        children?.each{ expectedChild ->
            if( allFound && !actualChildren.contains(expectedChild) ){
//                logger.debug("${expectedChild} is NOT found")
                allFound = false;
            }else if( allFound ){
//                logger.debug("${expectedChild} is found")
            }
        }
//        logger.debug("Does directory[${directory.canonicalPath}] contain? $allFound")
        return allFound
    }


    protected String getBaseDir() {
        File file = new File(".")
        logger.debug("Start looking for basedir at: ${file.canonicalPath}")
        while( file != null && !dirContainsChildren(file, "client-api", "client-implementations", "pom.xml") ) {
            file = new File(file.canonicalPath).getParentFile()
//            logger.debug(" Recursing up to potential basedir at: ${file?.canonicalPath}")
        }
        if( file == null ) {
            def children = new File(".").listFiles()
            children.each{ child ->
                if( file == null && dirContainsChildren(child, "client-api", "client-implementations", "pom.xml") )
                    file = child
            }
        }
        if( file == null )
            throw new FileNotFoundException("Unable to locate bae project home directory!")
        return file;
    }

    protected String getCertsDir() {
        return getBaseDir()+File.separator+"certs"
    }
    protected File getCertFile( String name ) throws FileNotFoundException {
        String certFilePath = getCertsDir() + File.separator + name
        logger.debug("Trying to load cert file path: $certFilePath")
        def certFile = new File(certFilePath)
        assertThat(certFile.exists(), is(Boolean.TRUE));
        return certFile
    }

    static {
        // According to https://wiki.shibboleth.net/confluence/display/OS30/Initialization+and+Configuration
        //   we initialize Open SAML 3 once and only once.
        logger.debug("Initializing opensaml 3...");
        InitializationService.initialize();
    }



    @Before
    public void startTest(){
        logger.info("============================== STARTING TEST ==============================")
    }
    @After
    public void stopTest(){
        logger.info("============================== STOPPING TEST ==============================\n\n")
    }


}
