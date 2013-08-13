package util.bcd;

import gtri.logging.Logger;
import gtri.logging.LoggerFactory;

import java.util.HashMap;

public class BCDIterator {

    private static final Logger logger = LoggerFactory.get(BCDIterator.class);

    public static final String START_SENTINEL = "SS";
    public static final String END_SENTINEL = "ES";
    public static final String FIELD_STOP = "FS";

    static HashMap<String, String> BCD_VALUES = new HashMap<String, String>();
    static{
        BCD_VALUES.put("00001", "0");
        BCD_VALUES.put("10000", "1");
        BCD_VALUES.put("01000", "2");
        BCD_VALUES.put("11001", "3");
        BCD_VALUES.put("00100", "4");
        BCD_VALUES.put("10101", "5");
        BCD_VALUES.put("01101", "6");
        BCD_VALUES.put("11100", "7");
        BCD_VALUES.put("00010", "8");
        BCD_VALUES.put("10011", "9");
        BCD_VALUES.put("11010", START_SENTINEL);
        BCD_VALUES.put("10110", FIELD_STOP);
        BCD_VALUES.put("11111", END_SENTINEL);
    }

    private String binaryString;
    private Integer index = 0;

    public BCDIterator(byte[] data){
        StringBuffer binaryStringBuffer = new StringBuffer();
        for( byte b : data ){
            binaryStringBuffer.append(String.format("%8s", Integer.toBinaryString((b + 256) % 256)).replace(' ', '0'));
        }
        binaryString = binaryStringBuffer.toString();
        if( (binaryString.length() % 5) != 0 ){
            logger.warn("Given BCD of improper length: "+binaryString.length()+", it should be a multiple of 5.");
            throw new IllegalArgumentException("Given BCD of improper length: "+binaryString.length()+", it should be a multiple of 5.");
        }

        logger.info("Constructed new BCDIterator on binary string: "+binaryString);
        index = 0;
    }

    public Integer size() {
        int size = 0;
        reset();
        while(hasMore()){
            size++;
            nextBCD();
        }
        reset();
        return size;
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        while( hasMore() ){
            String nextBCD = nextBCD();
            buffer.append(nextBCD);
        }
        return buffer.toString();
    }

    public Boolean hasMore() {
        return index < this.binaryString.length();
    }


    public String nextBCD() {
        String subString = this.binaryString.substring(index, index+5);
        index += 5;
//        logger.debug("Next 5 are: "+subString+", which translate into: "+BCD_VALUES.get(subString));
        return BCD_VALUES.get(subString);
    }//end nextBCD()

    public void reset(){
        this.index = 0;
    }


//    /**
//     * Returns the value of the current bit in the byte b at position pos.
//     */
//    private static Boolean valueAt(byte b, int pos) {
//        return (b & (1 << pos)) != 0;
//    }//end valueAt()

}