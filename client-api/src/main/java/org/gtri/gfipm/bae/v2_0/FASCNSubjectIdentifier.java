package org.gtri.gfipm.bae.v2_0;

import gtri.logging.Logger;
import gtri.logging.LoggerFactory;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.GeneralName;
import util.bcd.FASCNByteParser;

import java.io.IOException;
import java.io.StringWriter;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A helper class for creating {@link SubjectIdentifier} objects from incoming FASC-N values.
 * <br/><br/>
 * User: brad
 * Date: 4/6/13 10:20 AM
 */
public class FASCNSubjectIdentifier implements SubjectIdentifier {
    //==================================================================================================================
    //  Static Constants
    //==================================================================================================================
    private static Logger logger = LoggerFactory.get(FASCNSubjectIdentifier.class);
    public static final String FASCN_FORMAT = "urn:idmanagement.gov:icam:bae:v2:SAML:2.0:nameid-format:fasc-n";
    public static final Integer FASCN_LENGTH = 32;
    public static final String FASCN_OID = "2.16.840.1.101.3.6.6";
    //==================================================================================================================
    //  Public Static Methods
    //==================================================================================================================
    public static Boolean hasFASCN( X509Certificate cert ) throws CertificateParsingException, IOException {
        Collection<List<?>> altNamesColection = cert.getSubjectAlternativeNames();
        if( altNamesColection != null && !altNamesColection.isEmpty() ){
            logger.debug("Found @|cyan "+altNamesColection.size()+"|@ subjectAltNames, which might be FASC-N numbers...");
            for( List<?> subjectAltName : altNamesColection ){
//                logger.info("Subject Alt Name Found: "+subjectAltName);
                if( ((Integer) subjectAltName.get(0)) == GeneralName.otherName ){
                    ASN1InputStream asn1Input = new ASN1InputStream((byte[]) subjectAltName.get(1));
                    DERObject derObject = asn1Input.readObject();
//                    logger.info("Read DERObject["+derObject.getClass().getName()+"]: "+derObject);
                    if( derObject instanceof DERSequence ){
                        DERSequence sequence = (DERSequence) derObject;
                        if( sequence.size() != 2 ){
                            logger.warn("Cannot handle non-length 2 derSequence in subjectAltName: "+sequence);
                            continue;
                        }
                        DEREncodable oidNumberObj = sequence.getObjectAt(0);
                        if( oidNumberObj instanceof ASN1ObjectIdentifier ){
                            ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) oidNumberObj;
                            logger.info("Found SubjectALtName OID Number: "+oid.getId());
                            if( FASCN_OID.equals(oid.getId()) ){
                                logger.info("Found a FASC-N OID number "+oid.getId());
                                return true;
                            }
                        }else{
                            logger.warn("Expecting ASN1ObjectIdentifier but got "+oidNumberObj);
                            continue;
                        }
                    }else{
                        logger.warn("Cannot handle non-DERSequence object:" +derObject);
                        continue;
                    }
                }
            }
        }
        return false;
    }//end hasFASCN()
    //==================================================================================================================
    //  Instance Variables
    //==================================================================================================================
    private String fascnNumber;

    private Integer agencyCode;
    private Integer systemCode;
    private Integer credentialNumber;
    private Integer credentialSeries;
    private Integer individualCredentialIssue;
    private Long personId;
    private Integer orgCategory;
    private Integer orgId;
    private Integer personOrgAssociationCategory;
    //==================================================================================================================
    //  Getters
    //==================================================================================================================
    public String getFascnNumber() {
        return fascnNumber;
    }

    public Integer getAgencyCode() {
        return this.agencyCode;
    }
    public Integer getSystemCode() {
        return this.systemCode;
    }
    public Integer getCredentialNumber() {
        return this.credentialNumber;
    }
    public Integer getCredentialSeries() {
        return this.credentialSeries;
    }
    public Integer getIndividualCredentialIssue() {
        return this.individualCredentialIssue;
    }
    public Long getPersonIdentifier() {
        return this.personId;
    }
    public Integer getOrganizationalCategory() {
        return this.orgCategory;
    }
    public Integer getOrganizationalIdentifier() {
        return this.orgId;
    }
    public Integer getPersonOrganizationAssociationCategory() {
        return this.personOrgAssociationCategory;
    }
    //==================================================================================================================
    //  Setters
    //==================================================================================================================
    public void setFascnNumber(String facsnNumber) throws InvalidFASCNException {
        if( facsnNumber != null ) facsnNumber = facsnNumber.trim();
        this.validate(facsnNumber);
        this.fascnNumber = facsnNumber;

        this.agencyCode = Integer.parseInt(facsnNumber.substring(0, 4));
        this.systemCode = Integer.parseInt(facsnNumber.substring(4, 8));
        this.credentialNumber = Integer.parseInt(facsnNumber.substring(8, 14));
        this.credentialSeries = Integer.parseInt(facsnNumber.substring(14, 15));
        this.individualCredentialIssue = Integer.parseInt(facsnNumber.substring(15, 16));
        this.personId = Long.parseLong(facsnNumber.substring(16, 26));
        this.orgCategory = Integer.parseInt(facsnNumber.substring(26, 27));
        this.orgId = Integer.parseInt(facsnNumber.substring(27, 31));
        this.personOrgAssociationCategory = Integer.parseInt(facsnNumber.substring(31, 32));
    }

    public void setFascnNumber( Integer agencyCode, Integer systemCode, Integer credentialNumber, Integer credentialSeries,
                              Integer individualCredentialIssue, Long personId, Integer orgCategory, Integer orgId,
                              Integer personOrgAssociationCategory ) throws InvalidFASCNException {
        StringWriter facsnNumberWriter = new StringWriter();

        if( agencyCode > 9999 )
            throw new InvalidFASCNException(""+agencyCode, "FASC-N cannot have an agency code length greater than 4 digits.");
        else if( agencyCode < 0 )
            throw new InvalidFASCNException(""+agencyCode, "FASC-N cannot have a negative agency code.");
        facsnNumberWriter.append(String.format("%04d", agencyCode));

        if( systemCode > 9999 )
            throw new InvalidFASCNException(""+systemCode, "FASC-N cannot have a system code length greater than 4 digits.");
        else if( systemCode < 0 )
            throw new InvalidFASCNException(""+systemCode, "FASC-N cannot have a negative system code.");
        facsnNumberWriter.append(String.format("%04d", systemCode));

        if( credentialNumber > 999999 )
            throw new InvalidFASCNException(""+credentialNumber, "FASC-N cannot have a credential number length greater than 6 digits.");
        else if( credentialNumber < 0 )
            throw new InvalidFASCNException(""+credentialNumber, "FASC-N cannot have a negative credential number.");
        facsnNumberWriter.append(String.format("%06d", credentialNumber));

        if( credentialSeries > 9 )
            throw new InvalidFASCNException(""+credentialSeries, "FASC-N cannot have a credential series length greater than 1 digit.");
        else if( credentialSeries < 0 )
            throw new InvalidFASCNException(""+credentialSeries, "FASC-N cannot have a negative credential series.");
        facsnNumberWriter.append(String.format("%01d", credentialSeries));

        if( individualCredentialIssue > 9 )
            throw new InvalidFASCNException(""+individualCredentialIssue, "FASC-N cannot have an individual credential issue (credential code) length greater than 1 digit.");
        else if( individualCredentialIssue < 0 )
            throw new InvalidFASCNException(""+individualCredentialIssue, "FASC-N cannot have a negative individual credential issue (credential code).");
        facsnNumberWriter.append(String.format("%01d", individualCredentialIssue));

        if( personId > 9999999999l )
            throw new InvalidFASCNException(""+personId, "FASC-N cannot have a person identifier length greater than 10 digits.");
        else if( personId < 0l )
            throw new InvalidFASCNException(""+personId, "FASC-N cannot have a negative person identifier.");
        facsnNumberWriter.append(String.format("%010d", personId));

        if( orgCategory > 9 )
            throw new InvalidFASCNException(""+orgCategory, "FASC-N cannot have an organizational category length greater than 1 digit.");
        else if( orgCategory < 0 )
            throw new InvalidFASCNException(""+orgCategory, "FASC-N cannot have a negative organizational category.");
        facsnNumberWriter.append(String.format("%01d", orgCategory));

        if( orgId > 9 )
            throw new InvalidFASCNException(""+orgId, "FASC-N cannot have an organization id length greater than 4 digits.");
        else if( orgId < 0 )
            throw new InvalidFASCNException(""+orgId, "FASC-N cannot have a negative organizational id.");
        facsnNumberWriter.append(String.format("%04d", orgId));

        if( personOrgAssociationCategory > 9 )
            throw new InvalidFASCNException(""+personOrgAssociationCategory, "FASC-N cannot have a person organization association category length greater than 1 digit.");
        else if( personOrgAssociationCategory < 0 )
            throw new InvalidFASCNException(""+personOrgAssociationCategory, "FASC-N cannot have a negative person organization association category.");
        facsnNumberWriter.append(String.format("%01d", personOrgAssociationCategory));

        this.setFascnNumber(facsnNumberWriter.toString());
    }
    //==================================================================================================================
    //  Constructors
    //==================================================================================================================
    public FASCNSubjectIdentifier(String facsnNumber) throws InvalidFASCNException {
        this.setFascnNumber(facsnNumber);
    }
    public FASCNSubjectIdentifier(
            Integer agencyCode, Integer systemCode, Integer credentialNumber, Integer credentialSeries,
            Integer individualCredentialIssue, Long personId, Integer orgCategory, Integer orgId,
            Integer personOrgAssociationCategory
    ) throws InvalidFASCNException {
        this.setFascnNumber(agencyCode, systemCode, credentialNumber, credentialSeries, individualCredentialIssue,
                personId, orgCategory, orgId, personOrgAssociationCategory);
    }
    public FASCNSubjectIdentifier(X509Certificate cert) throws CertificateParsingException, IOException, InvalidFASCNException {
        Collection<List<?>> altNamesColection = cert.getSubjectAlternativeNames();
        if( altNamesColection != null && !altNamesColection.isEmpty() ){
            logger.debug("Found @|cyan "+altNamesColection.size()+"|@ subjectAltNames, which might be FASC-N numbers...");
            for( List<?> subjectAltName : altNamesColection ){
//                logger.info("Subject Alt Name Found: "+subjectAltName);
                if( ((Integer) subjectAltName.get(0)) == GeneralName.otherName ){
                    ASN1InputStream asn1Input = new ASN1InputStream((byte[]) subjectAltName.get(1));
                    DERObject derObject = asn1Input.readObject();
//                    logger.info("Read DERObject["+derObject.getClass().getName()+"]: "+derObject);
                    if( derObject instanceof DERSequence ){
                        DERSequence sequence = (DERSequence) derObject;
                        if( sequence.size() != 2 ){
                            logger.warn("Cannot handle non-length 2 derSequence in subjectAltName: "+sequence);
                            continue;
                        }
                        DEREncodable oidNumberObj = sequence.getObjectAt(0);
                        if( oidNumberObj instanceof ASN1ObjectIdentifier ){
                            ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) oidNumberObj;
                            logger.info("Found SubjectALtName OID Number: "+oid.getId());
                            if( FASCN_OID.equals(oid.getId()) ){
                                logger.info("Found a FASC-N OID number "+oid.getId());
//                                byte[] bytes = ((DERTaggedObject) ((DERTaggedObject) sequence.getObjectAt(1)).getObject()).getObject().getEncoded();
                                byte[] bytes = sequence.getObjectAt(1).getDERObject().getEncoded();
                                int size = 25;
                                if( bytes.length > size )
                                    bytes = Arrays.copyOfRange(bytes, bytes.length - size, bytes.length);
                                if( bytes.length < size ){
                                    logger.warn("Expecting "+size+" bytes for FASC-N identifier, but only found "+bytes.length);
                                    throw new CertificateParsingException("Expecting "+size+" bytes for FASC-N identifier, but only found "+bytes.length);
                                }
                                this.setFromBytes(bytes);
                                break;
                            }
                        }else{
                            logger.warn("Expecting ASN1ObjectIdentifier but got "+oidNumberObj);
                            continue;
                        }
                    }else{
                        logger.warn("Cannot handle non-DERSequence object:" +derObject);
                        continue;
                    }
                }
            }
        }
    }//end FASCNSubjectIdentifier

    final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    //==================================================================================================================
    //  Private/Protected Methods
    //==================================================================================================================
    protected void setFromBytes( byte[] bytes ) throws InvalidFASCNException {
        logger.info("Parsing OID from Bytes: "+bytesToHex(bytes));

        FASCNByteParser fascnByteParser = new FASCNByteParser(bytes);
        List<String> values = fascnByteParser.getValues();
        if( values.size() < 9 || values.size() > 10 ){
            logger.warn("Invalid number of values in FASCN BCD values.  Expecting 9 or 10, but got "+values.size());
            throw new UnsupportedOperationException("Invalid number of values in FASCN BCD values.  Expecting 9 or 10, but got "+values.size());
        }

        StringBuffer crammedTogether = new StringBuffer();
        for( int i = 0; i < 9; i++ ){
            crammedTogether.append(values.get(i));
        }

        this.setFascnNumber(crammedTogether.toString());

    }//end setFromBytes()


    protected void validate( String fascn ) throws InvalidFASCNException {
        if( fascn == null || fascn.length() == 0 )
            throw new InvalidFASCNException(fascn, "Empty FASC-N is not allowed.");
        if( fascn.length() < FASCN_LENGTH )
            throw new InvalidFASCNException(fascn, "FASC-N Number is too short.  It must be exactly "+FASCN_LENGTH+" numbers long, but this one is "+fascn.length());
        if( fascn.length() > FASCN_LENGTH )
            throw new InvalidFASCNException(fascn, "FASC-N Number is too long.  It must be exactly "+FASCN_LENGTH+" numbers long, but this one is "+fascn.length());
        if( !fascn.matches("[0-9]{"+FASCN_LENGTH+"}") )
            throw new InvalidFASCNException(fascn, "FASC-N Number is required to be numeric.  Non-numeric characters are not allowed.");


        Integer orgCateogryCode = Integer.parseInt(fascn.substring(26, 27));
        if( orgCateogryCode < 1 || orgCateogryCode > 4 )
            throw new InvalidFASCNException(fascn, "Organization Category must be 1, 2, 3 or 4.");

        Integer poa = Integer.parseInt(fascn.substring(31, 32));
        if( poa < 1 || poa > 7 )
            throw new InvalidFASCNException(fascn, "Person/Organization Association Category must be 1, 2, 3, 4, 5, 6, or 7.");

        // TODO Check for other valid values in FASC-N?

    }//end validate()

    //==================================================================================================================
    //  Public Methods
    //==================================================================================================================
    public String toString() {
        return "FASC-N SubjectId["+this.getFascnNumber()+"]";
    }

    public boolean equals(Object obj){
        if( this.getFascnNumber() == null ){
            throw new NullPointerException("FASCNSubjectIdentifier has a null FASC-N number.  This is not allowed.");
        }else if( obj instanceof SubjectIdentifier ){
            SubjectIdentifier that = (SubjectIdentifier) obj;
            return this.getFormat().equalsIgnoreCase(that.getFormat()) &&
                this.getName().equalsIgnoreCase(that.getName());
        }
        return false;
    }//end equals()

    public int hashCode(){
        return this.getName().hashCode();
    }
    //==================================================================================================================
    //  Interface Implementations
    //==================================================================================================================
    @Override
    public String getFormat() {
        return FASCN_FORMAT;
    }

    @Override
    public String getName() {
        return this.getFascnNumber();
    }



}//end FASCNSubjectIdentifier