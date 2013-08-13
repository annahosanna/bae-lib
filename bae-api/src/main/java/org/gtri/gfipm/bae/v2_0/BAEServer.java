package org.gtri.gfipm.bae.v2_0;

import java.util.Collection;

/**
 * The Backend Attribute Exchange Server Representation.  Provides all methods to access backend attributes from a BAE,
 * in accordance to the spec "Security Markup Language (SAML) 2.0 Identifier and Protocol Profiles for Backend Attribute
 * Exchange (BAE) v2.0".  see: http://www.idmanagement.gov/documents/BAE_v2_SAML2_Profile_Final_v1.0.0.pdf <br/>
 * <br/>
 * Usage of this class assumes that you have pre-configured it (it provides no configuration once created).  To obtain
 * instances of this class, you should use the {@link BAEServerFactory}.
 * <br/><br/>
 * User: slee35
 * Date: 4/6/2013 8:54 AM
 */
public interface BAEServer {

    /**
     * Allows setting the web service options after creation.
     * <br/><br/>
     * @param opts
     */
    public void setWebServiceRequestOptions(WebServiceRequestOptions opts);

    /**
     * The "AttributeQuery" method, which allows for querying attributes on the BAE Server based on a given {@link SubjectIdentifier}.
     * <br/><br/>
     * @param subjectId the {@link SubjectIdentifier} to query on
     * @return a {@link Collection} of {@link BackendAttribute} objects.
     * @throws BAEServerException for any mishaps, including IOExceptions, etc.
     */
    public Collection<BackendAttribute> attributeQuery( SubjectIdentifier subjectId ) throws BAEServerException;


}//end BackendAttributeExchangeServer