package spring_tests;

import java.lang.Character;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class LargeDocGenerator {
    public LargeDocGenerator(String largeStringFile) throws IOException {
	currentChar = 'a';
	rnd = new Random();

	byte[] fileAsByte = Files.readAllBytes(Paths.get(largeStringFile));
	largeString = new String(fileAsByte, StandardCharsets.UTF_8);
    }
    
    public LargeDoc generateNextDoc() {
	//System.console().printf("Generating new LargeDoc\n");
	lastDoc = new LargeDoc();

	lastDoc.testStringField = getCurrentString();
	
	lastDoc.binaryField = new byte[4096];
	rnd.nextBytes(lastDoc.binaryField);

	lastDoc.lotsOfStuff.add("Hrothgar");
	lastDoc.lotsOfStuff.add("king");
	lastDoc.lotsOfStuff.add("of");
	lastDoc.lotsOfStuff.add("the Danes");
	lastDoc.lotsOfStuff.add("or");
	lastDoc.lotsOfStuff.add("Scyldings, builds a great mead-hall");

	lastDoc.largeAmountOfText = largeString;
	
	//System.console().printf("Done generating new LargeDoc\n");
	return lastDoc;
    }

    private String getCurrentString() {
	if (currentChar  == 'z') {
	    currentChar = 'a';
	} else {
	    currentChar++;
	}
	return Character.toString(currentChar);
    }

    private LargeDoc lastDoc;
    private String   largeString;
    private Random   rnd;
    private char     currentChar;
}
