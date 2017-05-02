package spring_data_test;

//import org.springframework.data.annotation.Id;

public class LargeDoc {
    public String testStringField;
    public byte[] binaryField;
    public ArrayList<String> lotsOfStuff;

    public LargeDoc() {
	testStringField = new String("");
	binaryField = 0xDEADBEEF;
	lotsOfStuff = new ArrayList<String>();
    }

    public LargeDoc(String testField, byte[] binData) {
	testStringField = testField;
	binaryField = binData;
	lotsOfStuff = new ArrayList<String>();
    }
}
