package org.gtri.gfipm.bae.v2_0;

import java.util.List;

/**
 * Represents an attribute coming from the {@link BAEServer} on a successful query.
 * <br/><br/>
 * User: brad
 * Date: 4/6/13 9:00 AM
 */
public interface BackendAttribute {

    /**
     * Returns the @Name of the attribute as a String.  Note that this isn't even technically part of the specification,
     * so use with Caution.
     * <br/><br/>
     * @return a {@link String} with the name of this attribute, or null if not found.
     */
    public String getName();

    /**
     * A friendly name for this attribute.  May be null or empty, so use with caution (don't assume it exists).
     * <br/><br/>
     * @return a String with the friendly name, or null if not found.
     */
    public String getFriendlyName();

    /**
     * The NameFormat property for this attribute.  Must be set to one of the values from the {@link AttributeNameFormat} enum.
     * <br/><br/>
     * @return must be a valid value from the enum: {@link AttributeNameFormat}, or null if not found.
     */
    public AttributeNameFormat getNameFormat();

    /**
     * A convenience method to return the only value which exists.  Note that if there are multiple values, then this
     * method blindly ignores all but the first which can lead to problems.
     * <br/><br/>
     * @return an instance of {@link BackendAttributeValue} or null.
     */
    public BackendAttributeValue getValue();

    /**
     * Returns a {@link List} of all values sent from the server, as {@link BackendAttributeValue} objects.
     * <br/><br/>
     * @return a {@link List} of {@link BackendAttributeValue} objects.
     */
    public List<BackendAttributeValue> getValues();

}//end BackendAttribute