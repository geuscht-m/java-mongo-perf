package spring_tests;

import java.lang.System;
import java.io.IOException;
import java.util.LongSummaryStatistics;
import java.util.List;
import java.util.Vector;
import java.util.Collections;

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

	System.console().printf("Starting insert tests\n");
	runInsertTests();
	System.console().printf("Starting update via save tests\n");
	runUpdateViaSaveTests();
	runReadTests();
	runDeleteTests();
    }

    private void initialiseTests() throws IOException {
	System.console().printf("Initializing tests\n");
	docgenL = new LargeDocGenerator("metamorphosis-kafka.txt");
	largeDocs.deleteAll();
	System.console().printf("Done initializing tests\n");
    }
    
    private void runInsertTests() {
	Vector<Long> measuredTimes = new Vector<>(100001);
	long totalTime = 0;
	// Large docs first
	System.console().printf("Begin insert tests\n");
	for (int i = 0; i < 100001; i++) {
	    LargeDoc newDoc = docgenL.generateNextDoc();

	    long startNS = System.nanoTime();
	    //System.console().printf("Trying to save document\n");
	    largeDocs.save(newDoc);
	    long endNS = System.nanoTime();
	    measuredTimes.add(endNS - startNS);
	}

	Collections.sort(measuredTimes);
	LongSummaryStatistics stats = new LongSummaryStatistics();
	for (long v : measuredTimes) {
	    stats.accept(v);
	}
	System.console().printf("Total execution time (ms) : %d\n", stats.getSum() / 1000);
	System.console().printf("Average insert time (ms): %f\n", stats.getAverage() / 1000);
	System.console().printf("Median insert time (ms): %d\n", measuredTimes.elementAt(100001/2) / 1000);
    }

    private void runUpdateViaSaveTests() {
	Vector<Long> measuredTimes = new Vector<>(100001);
	System.console().printf("Begin update tests\n");
	List<LargeDoc> ldocs = largeDocs.findByTestStringField("a");
	for (LargeDoc doc : ldocs) {
	    doc.lotsOfStuff.add("or palace,");
	    doc.lotsOfStuff.add("in which he hopes");
	    doc.lotsOfStuff.add("to");
	    doc.lotsOfStuff.add("feast his liegemen");
	    doc.lotsOfStuff.add("and");
	    doc.lotsOfStuff.add("to");
	    doc.lotsOfStuff.add("give");
	    doc.lotsOfStuff.add("them");
	    doc.lotsOfStuff.add("presents.");

	    long startNS = System.nanoTime();
	    largeDocs.save(doc);
	    long endNS = System.nanoTime();
	    measuredTimes.add(endNS - startNS);
	}
	Collections.sort(measuredTimes);
	LongSummaryStatistics stats = new LongSummaryStatistics();
	for (long v : measuredTimes) {
	    stats.accept(v);
	}
	System.console().printf("Total   update via save execution time (ms): %d\n", stats.getSum() / 1000);
	System.console().printf("Average update via save insert time (ms): %f\n", stats.getAverage() / 1000);
	System.console().printf("Median  update via save insert time (ms): %d\n", measuredTimes.elementAt(measuredTimes.size()/2) / 1000);
    }

    private void runReadTests() {
    }

    private void runDeleteTests() {
    }

    private LargeDocGenerator docgenL;
}
