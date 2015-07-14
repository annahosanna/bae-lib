package org.gtri.gfipm.bae.v2_0;

import gtri.logging.Logger;
import gtri.logging.LoggerFactory;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.openssl.PEMReader;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assume.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public abstract class AbstractTest {

    static Logger logger = LoggerFactory.get(AbstractTest.class);

    static {
        java.security.Security.addProvider(new BouncyCastleProvider());
    }


    @Before
    public void startTest(){
        logger.info("============================== STARTING TEST ==============================");
    }
    @After
    public void stopTest(){
        logger.info("============================== STOPPING TEST ==============================\n\n");
    }


    static XMLGregorianCalendar toXMLGregorianCalendar(Calendar c) throws Exception {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(c.getTimeInMillis());
        XMLGregorianCalendar xc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        return xc;
    }


    X509CertificateObject readCert( String path ) throws Exception {
        X509CertificateObject cert = (X509CertificateObject) new PEMReader(new FileReader(path)).readObject();
        if( cert == null ){
            logger.warn("Cannot load certificate object: @|yellow "+path+"|@");
            throw new UnsupportedOperationException("Error reading certificate: "+path);
        }
        return cert;
    }
    X509CertificateObject readCert( File file ) throws Exception {
        X509CertificateObject cert = (X509CertificateObject) new PEMReader(new FileReader(file)).readObject();
        if( cert == null ){
            logger.warn("Cannot load certificate object: @|yellow "+file.getCanonicalPath()+"|@");
            throw new UnsupportedOperationException("Error reading certificate: "+file);
        }
        return cert;
    }


    protected Boolean dirContainsChildren( File directory, String ... children ){
        List<String> actualChildren = new ArrayList<String>();
        for( File file : directory.listFiles() ){
            actualChildren.add(file.getName());
        }
        boolean allFound = true;
        if( children != null && children.length > 0 ) {
            for (String expectedChild : children) {
                if (allFound && !actualChildren.contains(expectedChild)) {
                    //                logger.debug("${expectedChild} is NOT found")
                    allFound = false;
                } else if (allFound) {
                    //                logger.debug("${expectedChild} is found")
                }
            }
        }
//        logger.debug("Does directory[${directory.canonicalPath}] contain? $allFound")
        return allFound;
    }

    protected String getBaseDir() throws Exception {
        File file = new File(".");
        logger.debug("Start looking for basedir at: "+file.getCanonicalPath());
        while( file != null && !dirContainsChildren(file, "client-api", "client-implementations", "pom.xml") ) {
            file = new File(file.getCanonicalPath()).getParentFile();
//            logger.debug(" Recursing up to potential basedir at: ${file?.canonicalPath}")
        }
        if( file == null ) {
            File[] children = new File(".").listFiles();
            if( children != null && children.length > 0 ) {
                for( File child : children ){
                    if (file == null && dirContainsChildren(child, "client-api", "client-implementations", "pom.xml"))
                        file = child;
                }
            }
        }
        if( file == null )
            throw new FileNotFoundException("Unable to locate bae project home directory!");
        return file.getCanonicalPath();
    }
    protected String getCertsDir() throws Exception {
        return getBaseDir()+File.separator+"certs";
    }
    protected File getCertFile( String name ) throws Exception {
        String certFilePath = getCertsDir() + File.separator + name;
        logger.debug("Trying to load cert file path: "+certFilePath);
        File certFile = new File(certFilePath);
        assumeTrue(certFile.exists());
        return certFile;
    }

    protected File getCertFileAbsolute( String certFilePath ) throws Exception {
        logger.debug("Trying to load cert file path: "+certFilePath);
        File certFile = new File(certFilePath);
        assumeTrue(certFile.exists());
        logger.debug("Found certificate file: "+certFile.getCanonicalPath());
        return certFile;
    }

}
