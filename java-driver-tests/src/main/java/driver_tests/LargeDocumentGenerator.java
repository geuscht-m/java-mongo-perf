package driver_tests;

import java.lang.Character;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import org.bson.Document;
import java.util.Arrays;

public class LargeDocumentGenerator implements DocumentGenerator {
    public LargeDocumentGenerator(String largeStringFile) throws IOException {
	currentChar = 'a';
	rnd = new Random();

	byte[] fileAsByte = Files.readAllBytes(Paths.get(largeStringFile));
	largeString = new String(fileAsByte, StandardCharsets.UTF_8);
    }
    
    public Document generateNextDocument() {
	byte[] binField = new byte[4096];
	rnd.nextBytes(binField);

	//System.console().printf("Generating new LargeDocument\n");
	Document doc = new Document("testStringField", getCurrentString())
	    .append("binaryField", binField)
	    .append("largeAmountOfText", largeString)
	    .append("lotsOfStuff", Arrays.asList("Hrothgar", "king", "of", "the Danes", "or", "Scyldings, builds a great mead-hall"));
	
	return doc;
    }

    private String getCurrentString() {
	if (currentChar  == 'z') {
	    currentChar = 'a';
	} else {
	    currentChar++;
	}
	return Character.toString(currentChar);
    }

    private String   largeString;
    private Random   rnd;
    private char     currentChar;
}
