package util.bcd;


import gtri.logging.Logger;
import gtri.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FASCNByteParser{

    public static final Logger logger = LoggerFactory.get();

    BCDIterator iterator = null;

    public FASCNByteParser(byte[] data){
        iterator = new BCDIterator(data);
    }

    public List<String> getValues() {
        List<String> values = new ArrayList<String>();

        StringBuffer currentStringBuffer = new StringBuffer();
        int bcdSize = iterator.size();
        logger.debug("Parsing " + bcdSize + " BCD values...");
        for( int i = 0; i < bcdSize; i++ ){
            String next = iterator.nextBCD();
            logger.debug("Value at [%d] is [%s]...", i, next);
            if( next == null || next.trim().length() == 0 ){
                logger.warn("Encountered a null/empty byte, just skipping...");
                continue;
            }
            if( next.equals(BCDIterator.START_SENTINEL) ){
                if( i != 0 ){
                    throw new UnsupportedOperationException("Encountered unexpected start sentinel at position '"+i+"' in FASC-N BCD.");
                }
            }else if( next.equals(BCDIterator.FIELD_STOP) || next.equals(BCDIterator.END_SENTINEL) ){
                if( values.size() < 5 ) {
                    values.add( currentStringBuffer.toString() );
                    currentStringBuffer = new StringBuffer();
                }else{
                    String combinedString = currentStringBuffer.toString();
                    String pi = combinedString.substring(0, 10);
                    values.add( pi );
                    String oc = combinedString.substring(10, 11);
                    values.add( oc );
                    String oi = combinedString.substring(11, 15);
                    values.add( oi );
                    String poa = combinedString.substring(15, 16);
                    values.add( poa );
                    currentStringBuffer = new StringBuffer();
                }
            }else{
                currentStringBuffer.append(next);
            }
        }
        if( currentStringBuffer.length() > 0 ){
            values.add( currentStringBuffer.toString() );
        }

        logger.info("Parsed values: ");
        for( int i = 0; i < values.size(); i++ ){
            logger.info("  ["+i+"] => ["+values.get(i)+"]");
        }

        return values;
    }


}
