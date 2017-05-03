package spring_tests;

import java.util.Random;

public class LargeDocGenerator {
    public LargeDocGenerator() {
	rnd = new Random();
    }
    
    public LargeDoc generateNextDoc() {
	lastDoc = new LargeDoc();
	
	lastDoc.binaryField = new byte[1025];
	rnd.nextBytes(lastDoc.binaryField);

	lastDoc.lotsOfStuff.add("a");
	lastDoc.lotsOfStuff.add("b");
	lastDoc.lotsOfStuff.add("c");
	
	return lastDoc;
    }

    private LargeDoc lastDoc;
    private String   randomString;
    private Random   rnd;
}
