package spring_tests;

import java.util.ArrayList;
//import java.util.Random;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class LargeDoc {

    @Id
    public String Id;

    @Indexed
    public String testStringField;
    public byte[] binaryField;
    public ArrayList<String> lotsOfStuff;
    public String largeAmountOfText;

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
