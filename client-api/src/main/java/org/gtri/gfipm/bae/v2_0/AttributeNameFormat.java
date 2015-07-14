package org.gtri.gfipm.bae.v2_0;

/**
 * @See section 4.3.2 on page 25 of 49 in in the spec "BAE_v2_SAML2_Profile_Final_v1.0.0".
 * <br/><br/>
 * User: brad
 * Date: 4/6/13 9:02 AM
 */
public enum AttributeNameFormat {
    unspecified("urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified"),
    uri("urn:oasis:names:tc:SAML:2.0:attrname-format:uri"),
    basic("urn:oasis:names:tc:SAML:2.0:attrname-format:basic");

    private String formalFormatURI;

    private AttributeNameFormat(String formalFormatURI){
        this.formalFormatURI = formalFormatURI;
    }

    public String getFormalFormatURI() {
        return formalFormatURI;
    }


    public static AttributeNameFormat fromUri( String uri ){
        AttributeNameFormat format = null;
        for( AttributeNameFormat current : AttributeNameFormat.values() ){
            if( current.getFormalFormatURI().equalsIgnoreCase(uri) )
                format = current;
        }
        return format;
    }

}//end AttributeNameFormat