package org.gtri.gfipm.bae.v2_0;

import org.w3c.dom.Document;

import java.util.Calendar;

/**
 * Represents a value for a {@link BackendAttribute}.  This class is interesting, because it represents an abstract
 * or polymorphic type.
 * <br/><br/>
 * User: brad.lee@gtri.gatech.edu
 * Date: 2013/05/02 3:21 PM
 */
public interface BackendAttributeValue {

    /**
     * Returns the string representation of this value.  Note that this is the only method which does NOT throw
     * an exception.
     * <br/><br/>
     * @return the string representing this value, even null or empty if applicable.
     */
    public String getStringValue();

    /**
     * Returns a boolean value if possible, otherwise an exception is raised.
     * <br/><br/>
     * @return true or false, otherwise an exception.  Never null.
     */
    public Boolean getBooleanValue() throws ValueCoerceException;

    /**
     *  Attempts to coerce this value into a number.  If possible, a number is returned.  Else a an exception is raised.
     *  <br/><br/>
     * @return a number or exception, Never null.
     * @throws ValueCoerceException if the value isn't a number.
     */
    public Number getNumericValue() throws ValueCoerceException;

    /**
     * Attempts to Coerce the string value into the default date format used by the {@link java.text.SimpleDateFormat} class.
     * If possible, it will then be parsed into a Date object and set into a Calendar.
     * <br/><br/>
     * @return a {@link java.util.Calendar} if possible, exception otherwise.
     * @throws ValueCoerceException for any errors.
     */
    public Calendar getDateValue() throws ValueCoerceException;

    /**
     * Attempts to Coerce the string value into the default date format used by the {@link java.text.SimpleDateFormat} class.
     * If possible, it will then be parsed into a Date object and set into a Calendar.
     * <br/><br/>
     * @param datePattern the {@link String} date pattern used to parse the date from the string value, based on the {@link java.text.SimpleDateFormat} class.
     * @return a {@link java.util.Calendar} if possible, exception otherwise.
     * @throws ValueCoerceException for any errors.
     */
    public Calendar getDateValue(String datePattern) throws ValueCoerceException;

    /**
     * Attempts to return this value as an XML Document, which is only valid if the value is XML.  If not, an exception
     * is thrown.
     * <br/><br/>
     * @return a {@link Document} or an exception is thrown.  Never null.
     * @throws ValueCoerceException if the value is not an XML snippet
     */
    public Document getXmlValue() throws ValueCoerceException;



    //==================================================================================================================
    //  XML Helper Methods
    //      In the event that the value which is returned is truly XML, then these methods will assist you to
    //      get at the data.
    //==================================================================================================================
    // TODO Provide a method for setting the namespace to prefix mappings for the xpaths which will run.

//    public Boolean selectBoolean( String xpath ) throws ValueCoerceException;
//    public String selectString( String xpath ) throws ValueCoerceException;
//    public Node selectNode( String xpath ) throws ValueCoerceException;
//    public List<Node> selectNodes( String xpath ) throws ValueCoerceException;
//    public Element selectElement( String xpath ) throws ValueCoerceException;
//    public List<Element> selectElements( String xpath ) throws ValueCoerceException;


}//end BackendAttributeValue()