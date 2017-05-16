package driver_tests;

import java.lang.Character;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import org.bson.Document;
import java.util.Arrays;

class SmallDocumentGenerator {
    public SmallDocumentGenerator(String datafile) throws IOException {
	currDocNum = 1;
	wordIndex  = 0;
	
	byte[] fileAsByte = Files.readAllBytes(Paths.get(datafile));
	String smallString = new String(fileAsByte, StandardCharsets.UTF_8);
	words = smallString.split("\\s+");
    }

    public Document generateNextDocument() {
	double ratio = currDocNum / 3.1415;
	++currDocNum;

	Document smallDoc = new Document("frontSpring", words[wordIndex])
	    .append("rearSpring", words[wordIndex + 1])
	    .append("ratio", ratio);
	wordIndex += 2;

	if ((wordIndex + 2) >= words.length) {
	    wordIndex = 0;
	}
	return smallDoc;
    }

    private String[] words;

    private long     currDocNum;
    private int      wordIndex;
}
