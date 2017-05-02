package spring_tests;

import java.lang.System;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication implements CommandLineRunner {

    @Autowired
    private SmallDocRepository smallDocs;
    @Autowired
    private LargeDocRepository largeDocs;

    public static void main(String[] args) {
	SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
	initialiseTests();
	
	runInsertTests();
	runUpdateTests();
	runReadTests();
	runDeleteTests();
    }

    private void initialiseTests() {
	largeDocs.deleteAll();
    }
    
    private void runInsertTests() {
	long totalTime = 0;
	// Large docs first
	for (int i = 0; i < 100000; i++) {
	    LargeDoc newDoc = docgenL.generateNextDoc();

	    long startNS = System.nanoTime();
	    largeDocs.save(newDoc);
	    long endNS = System.nanoTime();
	    totalTime += endNS - startNS;
	}
	System.console().printf("Total execution time (ms) : %d\n", totalTime / 1000);
    }

    private void runUpdateTests() {
    }

    private void runReadTests() {
    }

    private void runDeleteTests() {
    }

    private LargeDocGenerator docgenL;
}
