package org.gtri.gfipm.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.bouncycastle.jce.provider.X509CertificateObject
import org.bouncycastle.openssl.PEMReader
import org.junit.After
import org.junit.Before
import static org.junit.Assume.*

import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

import org.bouncycastle.jce.provider.BouncyCastleProvider



abstract class AbstractTest {

    static Logger logger = LoggerFactory.get(AbstractTest)

    static {
        java.security.Security.addProvider(new BouncyCastleProvider());
    }


    @Before
    public void startTest(){
        logger.info("============================== STARTING TEST ==============================")
    }
    @After
    public void stopTest(){
        logger.info("============================== STOPPING TEST ==============================\n\n")
    }


    static XMLGregorianCalendar toXMLGregorianCalendar(Calendar c){
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(c.getTimeInMillis());
        XMLGregorianCalendar xc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        return xc;
    }


    X509CertificateObject readCert( String path ){
        X509CertificateObject cert = new PEMReader(new FileReader(path)).readObject()
        if( cert == null ){
            logger.warn("Cannot load certificate object: @|yellow ${path}|@")
            throw new UnsupportedOperationException("Error reading certificate: "+path);
        }
        return cert;
    }
    X509CertificateObject readCert( File file ){
        X509CertificateObject cert = new PEMReader(new FileReader(file)).readObject()
        if( cert == null ){
            logger.warn("Cannot load certificate object: @|yellow ${file.canonicalPath}|@")
            throw new UnsupportedOperationException("Error reading certificate: "+file);
        }
        return cert;
    }


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
        while( file != null && !dirContainsChildren(file, "bae-api", "implementations", "pom.xml") ) {
            file = new File(file.canonicalPath).getParentFile()
//            logger.debug(" Recursing up to potential basedir at: ${file?.canonicalPath}")
        }
        if( file == null ) {
            def children = new File(".").listFiles()
            children.each{ child ->
                if( file == null && dirContainsChildren(child, "bae-api", "implementations", "pom.xml") )
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
        assumeTrue(certFile.exists())
        return certFile
    }

}
