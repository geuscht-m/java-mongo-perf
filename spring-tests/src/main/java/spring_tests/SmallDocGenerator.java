package spring_tests;

import java.lang.Character;
//import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class SmallDocGenerator {
    public SmallDocGenerator(String smallStringFile) throws IOException {
	currDocNum = 1;
	wordIndex  = 0;
	
	byte[] fileAsByte = Files.readAllBytes(Paths.get(smallStringFile));
	String smallString = new String(fileAsByte, StandardCharsets.UTF_8);
	words = smallString.split("\\s+");
	//System.console().printf("Generated words array with length %d\n", words.length);
    }

    public SmallDoc generateNextDoc() {
	double ratio = currDocNum / 3.1415;
	++currDocNum;

	//System.console().printf("Generating small doc with wordIndex %d, array length is %d\n", wordIndex, words.length);
	
	SmallDoc smallDoc = new SmallDoc(words[wordIndex], words[wordIndex + 1], ratio);
	wordIndex += 2;

	if ((wordIndex + 2) >= words.length) {
	    //System.console().printf("Resetting word index\n");
	    wordIndex = 0;
	}
	return smallDoc;
    }
    
    private String[] words;
    private long     currDocNum;
    private int      wordIndex;
}
					       
