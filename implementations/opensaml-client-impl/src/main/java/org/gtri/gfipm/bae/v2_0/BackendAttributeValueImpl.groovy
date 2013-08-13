package org.gtri.gfipm.bae.v2_0

import org.w3c.dom.Document

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.text.SimpleDateFormat

class BackendAttributeValueImpl implements BackendAttributeValue {


    private String value
    private String xsiType

    public BackendAttributeValueImpl(String value){
        this(null, value);
    }
    public BackendAttributeValueImpl(String xsiType, String value){
        this.xsiType = xsiType;
        this.value = value;
    }



    public String toString(){
        return "BackendAttributeValue[${this.value}]"
    }

    public int hashCode(){
        return this.value.hashCode();
    }

    public boolean equals(Object obj){
        if( obj != null && obj instanceof BackendAttributeValueImpl ){
            return this.value.equals(obj.value)
        }
        return false
    }


    @Override
    String getStringValue() {
        return value;
    }

    @Override
    Boolean getBooleanValue() throws ValueCoerceException {

        // NOTE: This is a special GFIPM case, to make life easier.  I am not sure this is supposed to go here.
        if( this.value.equals("1") )
            return true;
        else if( this.value.equals("0") )
            return false;


        try{
            return Boolean.parseBoolean(value)
        }catch(Throwable t){
            ValueCoerceException vce = new ValueCoerceException(t);
            vce.setCoersionType(Boolean.class);
            vce.setValue(value);
            throw vce;
        }
    }

    @Override
    Number getNumericValue() throws ValueCoerceException {
        try{
            return Double.parseDouble(value)
        }catch(Throwable t){
            ValueCoerceException vce = new ValueCoerceException(t);
            vce.setCoersionType(Double.class);
            vce.setValue(value);
            throw vce;
        }
    }

    @Override
    Calendar getDateValue() throws ValueCoerceException {
        try{
            SimpleDateFormat formatter = new SimpleDateFormat();
            Date date = formatter.parse(this.value)
            Calendar c = Calendar.getInstance()
            c.setTime(date);
            return c
        }catch(Throwable t){
            ValueCoerceException vce = new ValueCoerceException(t);
            vce.setCoersionType(Calendar.class);
            vce.setValue(value);
            throw vce;
        }
    }

    @Override
    Calendar getDateValue(String datePattern) throws ValueCoerceException {
        try{
            SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
            Date date = formatter.parse(this.value)
            Calendar c = Calendar.getInstance()
            c.setTime(date);
            return c
        }catch(Throwable t){
            ValueCoerceException vce = new ValueCoerceException(t);
            vce.setCoersionType(Calendar.class);
            vce.setValue(value);
            throw vce;
        }
    }

    @Override
    Document getXmlValue() throws ValueCoerceException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        factory.setNamespaceAware(true)
        DocumentBuilder builder = factory.newDocumentBuilder()
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(this.value.getBytes())
        return builder.parse(bytesIn);
    }


}//end BackendAttributeValueImpl