package org.gtri.gfipm.bae.v2_0

import gtri.logging.Logger
import gtri.logging.LoggerFactory
import org.w3c.dom.Document
import org.w3c.dom.Element
import net.shibboleth.utilities.java.support.xml.SerializeSupport

import javax.xml.namespace.NamespaceContext
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

/**
 * Responsible for parsing backend attribute values from XML strings.
 *
 * Note that this link:
 *    http://www.ibm.com/developerworks/library/x-javaxpathapi/index.html
 *
 * Is very useful to understand the code in this class.
 */
class BackendAttributeParser {

    static Logger logger = LoggerFactory.get(BackendAttributeParser)

    static XPathExpression ALL_ATTRIBUTE_VALUE_ELEMENTS = null
    static XPathExpression XSI_TYPE_ATTRIBUTE = null

    static {
        try{
            ALL_ATTRIBUTE_VALUE_ELEMENTS = create("/saml2:Attribute/saml2:AttributeValue")
            XSI_TYPE_ATTRIBUTE = create("string(./@xsi:type)")

        }catch(Throwable T){
            logger.error("Error initializing xpaths!", T);
        }
    }

    protected static XPathExpression create(String xpathText){
        XPathFactory factory = XPathFactory.newInstance()
        XPath xpath = factory.newXPath()
        xpath.setNamespaceContext(new SimpleNamespaceContext())
        return xpath.compile(xpathText)
    }

    public static List<BackendAttributeValue> parseValues( String xmlString ){
        def attributeValues = []

        logger.debug("Parsing attribute values from XML: \n${xmlString}");
        Document doc = toDocument(xmlString)
        Element rootElement = doc.getDocumentElement()
        if( !rootElement.getTagName().contains("Attribute") ){
            logger.warn("Server has returned invalid XML: \n"+xmlString);
            throw new BAEServerException("Expecting saml2:Attribute, but server returned invalid XML: \n"+xmlString);
        }

        List<Element> valueElements = selectElements(rootElement, ALL_ATTRIBUTE_VALUE_ELEMENTS)

        valueElements.each{ Element valueElement ->
            logger.debug("Found value element: \n"+SerializeSupport.prettyPrintXML(valueElement))
            String xsiType = selectString(valueElement, XSI_TYPE_ATTRIBUTE)
            if( !xsiType )
                xsiType = "" // set it to empty instead of null.
            if( containsElements(valueElement) ){
                BackendAttributeValueImpl valueImpl = new BackendAttributeValueImpl("", XMLHelper.prettyPrintXML(valueElement));
                attributeValues.add( valueImpl );
            }else{
                logger.debug("XSI Type is set to: [@|cyan ${xsiType}|@] parsing value...")
                String textContent = valueElement.getTextContent();
                textContent = textContent.trim();
                BackendAttributeValueImpl valueImpl = new BackendAttributeValueImpl(xsiType, textContent);
                attributeValues.add( valueImpl );
            }

        }

        return attributeValues;
    }//end parseValues()


    protected static Document toDocument( String xmlString ){
        logger.debug("Pasring to W3C Document...")
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        factory.setNamespaceAware(true)
        DocumentBuilder builder = factory.newDocumentBuilder()
        return builder.parse(new ByteArrayInputStream(xmlString.getBytes()));
    }//end toDocument()


    protected static Boolean containsElements( Element element ){
        org.w3c.dom.NodeList children = element.getChildNodes();
        for( int i = 0; i < children.getLength(); i++ ){
            org.w3c.dom.Node child = children.item(i);
            if( child instanceof Element )
                return true;
        }
    }//end containsElements()


    protected static List<Element> selectElements( Element context, XPathExpression expr ){
        def elements = []
        org.w3c.dom.NodeList resultNodes = (org.w3c.dom.NodeList) expr.evaluate(context, XPathConstants.NODESET)
        for( int i = 0; i < resultNodes.getLength(); i++ ){
            org.w3c.dom.Node node = resultNodes.item(i);
            if( node instanceof Element )
                elements.add( node );
        }
        return elements;
    }//end selectElements()

    protected static String selectString( Element context, XPathExpression expr ){
        return (String) expr.evaluate(context, XPathConstants.STRING)
    }//end selectElements()







    static class SimpleNamespaceContext implements NamespaceContext {

        public static final Map<String, List<String>> XMLNS_BINDINGS = [
                "urn:oasis:names:tc:SAML:2.0:assertion" : ["saml2"],
                "http://www.w3.org/2001/XMLSchema-instance" : ["xsi"]
        ];

        @Override
        String getNamespaceURI(String prefix) {
            String theURI = null
            XMLNS_BINDINGS.keySet().each{ uri ->
                XMLNS_BINDINGS.get(uri).each{ currentPrefix ->
                    if( !theURI && prefix.equals(currentPrefix) )
                        theURI = uri;
                }
            }
            return theURI;
        }

        @Override
        String getPrefix(String namespaceURI) {
            return getPrefixes(namespaceURI)?.next()
        }

        @Override
        Iterator<String> getPrefixes(String namespaceURI) {
            return XMLNS_BINDINGS.get(namespaceURI)?.iterator()
        }
    }


}//end BackendAttributeParser()
