package org.gtri.gfipm.bae.v2_0;

/**
 * Contains detailed configuration relating the web service requests which are being sent.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/05/01 10:33 AM
 */
public interface WebServiceRequestOptions {

    //==================================================================================================================
    //  Here are the available options, their default values & descriptions...
    //==================================================================================================================
    /**
     * Whether or not we should ignore failures relating to the BAE server's WS Security headers.
     * true if we should fail if the BAE Server returns faulty headers, false if we should ignore and allow the request
     * to complete.  No matter what, errors will be printed to the log in a conspicuous manner.
     */
    public static final String STRICT_SERVER_WS_SECURITY_CHECKS = "STRICT_SERVER_WS_SECURITY_CHECKS";
    public static final Boolean STRICT_SERVER_WS_SECURITY_CHECKS_DEFAULT = Boolean.TRUE;

    /**
     * Whether or not to initiate the HttpClient connection with a client provided certificate for TLS Authentication.
     */
    public static final String CLIENT_CERT_AUTH = "CLIENT_CERT_AUTH";
    public static final Boolean CLIENT_CERT_AUTH_DEFAULT = Boolean.TRUE;

    /**
     * Whether or not to validate the server provided certificate for TLS Authentication.
     */
    public static final String SERVER_CERT_AUTH = "SERVER_CERT_AUTH";
    public static final Boolean SERVER_CERT_AUTH_DEFAULT = Boolean.TRUE;

    /**
     * DUMP_RESPONSE_XML tells the system whether it should dump the response assertion XML to a file (unencrypted).
     * This is useful for debugging purposes.  If it's true, then DUMP_RESPONSE_XML_FILE is assumed to have the file
     * which should be filled with the XML, overwritten if necessary.  If the file is not specified, the error condition
     * is silently ignored.
     */
    public static final String DUMP_RESPONSE_XML = "DUMP_RESPONSE_XML";
    public static final Boolean DUMP_RESPONSE_XML_DEFAULT = Boolean.FALSE;
    public static final String DUMP_RESPONSE_XML_FILE = "DUMP_RESPONSE_XML_FILE";


    /**
     * The algorithm to use for calculating the web services security signature.
     */
    public static final String WSS_SIGNATURE_ALGORITHM = "WSS_SIGNATURE_ALGORITHM";
    public static final String WSS_SIGNATURE_ALGORITHM_DEFAULT = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

    /**
     * Canonicalization algorithm for web services security
     */
    public static final String WSS_CANONICALIZATION_ALGORITHM = "WSS_CANONICALIZATION_ALGORITHM";
    public static final String WSS_CANONICALIZATION_ALGORITHM_DEFAULT = "http://www.w3.org/2001/10/xml-exc-c14n#"; // OMIT COMMENTS


    //==================================================================================================================
    //  This interface acts like a map, so here are the access methods...
    //==================================================================================================================
    public Boolean getBoolean(String key);
    public Boolean getBoolean(String key, Boolean defaultValue);

    public Number getNumber(String key);
    public Number getNumber(String key, Number defaultValue);

    public String getString(String key);
    public String getString(String key, String defaultValue);

    /**
     * Causes the web service request options to print their state to the console for debugging.
     */
    public void debugPrint();

}//end WebServiceRequestOptions