//package spring_data_test;

import java.util.ArrayList;
import org.springframework.data.annotation.Id;

public class LargeDoc {

    @Id
    public String Id;
    
    public String testStringField;
    public byte[] binaryField;
    public ArrayList<String> lotsOfStuff;

    public LargeDoc() {
	testStringField = new String("");
	binaryField = intToByteArray(0xDEADBEEF);
	lotsOfStuff = new ArrayList<String>();
    }

    public LargeDoc(String testField, byte[] binData) {
	testStringField = testField;
	binaryField = binData;
	lotsOfStuff = new ArrayList<String>();
    }

    private byte[] intToByteArray(int to_convert) {
	return new byte[] {
	    (byte) ((to_convert >> 24) & 0xFF),
	    (byte) ((to_convert >> 16) & 0xFF),   
	    (byte) ((to_convert >> 8) & 0xFF),   
	    (byte) (to_convert & 0xFF)
	};
    }
}
