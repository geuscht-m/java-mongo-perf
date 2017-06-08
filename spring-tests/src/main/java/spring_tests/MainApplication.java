package spring_tests;

import java.lang.System;
import java.io.IOException;
import java.util.LongSummaryStatistics;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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
	System.console().printf("Starting update via updateFirst() tests\n");
	runUpdateViaSingleUpdateTests();
	runReadTests();
	runDeleteTests();
    }

    private void initialiseTests() throws IOException {
	System.console().printf("Initializing tests\n");
	measuredTimes = new ArrayList<Long>(100001);
	docgenL = new LargeDocGenerator("metamorphosis-kafka.txt");
	docGenS = new SmallDocGenerator("huck-finn.txt");

	largeDocs.deleteAll();
	smallDocs.deleteAll();
	
	System.console().printf("Done initializing tests\n");
    }
    
    private void runInsertTests() {
	measuredTimes.clear();

	// Large docs first
	System.console().printf("Begin large document insert tests\n");
	for (int i = 0; i < 100001; i++) {
	    LargeDoc newDoc = docgenL.generateNextDoc();

	    long startNS = System.nanoTime();
	    //System.console().printf("Trying to save document\n");
	    largeDocs.save(newDoc);
	    measuredTimes.add(System.nanoTime() - startNS);
	}

	Collections.sort(measuredTimes);
	LongSummaryStatistics stats = new LongSummaryStatistics();
	for (long v : measuredTimes) {
	    stats.accept(v);
	}
	System.console().printf("Large Documents: Total execution time (ms) : %d\n", stats.getSum() / 1000000);
	System.console().printf("Large Documents: Average insert time (ms):   %f\n", stats.getAverage() / 1000000.0);
	System.console().printf("Large Documents: Median insert time (ms):    %f\n", measuredTimes.get(100001/2) / 1000000.0);

	measuredTimes.clear();

	System.console().printf("Begin small document insert tests\n");

	for (int i = 0; i < 200001; i++) {
	    SmallDoc newDoc = docGenS.generateNextDoc();

	    long startNS = System.nanoTime();
	    smallDocs.save(newDoc);
	    measuredTimes.add(System.nanoTime() - startNS);
	}

	Collections.sort(measuredTimes);
	stats = new LongSummaryStatistics();
	for (long v : measuredTimes) {
	    stats.accept(v);
	}
	System.console().printf("Small Documents: Total execution time (ms) : %d\n", stats.getSum() / 1000000);
	System.console().printf("Small Documents: Average insert time (ms):   %f\n", stats.getAverage() / 1000000.0);
	System.console().printf("Small Documents: Median insert time (ms):    %f\n", measuredTimes.get(100001/2) / 1000000.0);
    }

    private void runUpdateViaSaveTests() {
	measuredTimes.clear();
	
	System.console().printf("Begin large document update via save tests\n");
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
	System.console().printf("Total   large document update via save execution time (ms): %d\n", stats.getSum() / 1000000);
	System.console().printf("Average large document update via save insert time (ms): %f\n", stats.getAverage() / 1000000.0);
	System.console().printf("Median  large document update via save insert time (ms): %f\n", measuredTimes.get(measuredTimes.size()/2) / 1000000.0);

	measuredTimes.clear();
	System.console().printf("Begin small document update via save test\n");
	List<SmallDoc> sdocs = smallDocs.findAll();
	for (SmallDoc doc : sdocs) {
	    doc.frontSpring = doc.rearSpring;
	    long startNS = System.nanoTime();
	    smallDocs.save(doc);
	    long endNS = System.nanoTime();
	    measuredTimes.add(endNS - startNS);
	}

	Collections.sort(measuredTimes);
	stats = new LongSummaryStatistics();
	for (long v : measuredTimes) {
	    stats.accept(v);
	}
	System.console().printf("Total   small document update via save execution time (ms): %d\n", stats.getSum() / 1000000);
	System.console().printf("Average small document update via save insert time (ms): %f\n", stats.getAverage() / 1000000.0);
	System.console().printf("Median  small document update via save insert time (ms): %f\n", measuredTimes.get(measuredTimes.size()/2) / 1000000.0);
    }

    private void runUpdateViaSingleUpdateTests() {
	System.console().printf("Begin update via updateFirst tests\n");
	
	measuredTimes.clear();
	ApplicationContext ctx =  new AnnotationConfigApplicationContext(BasicMongoConfiguration.class);
	MongoOperations mongoOp = (MongoOperations)ctx.getBean("mongoTemplate");

	List<LargeDoc> ldocs = largeDocs.findByTestStringField("b");
	for (LargeDoc doc : ldocs) {
	    Query q = new Query();
	    q.addCriteria(Criteria.where("_id").is(doc.Id));
	    Update up = new Update();
	    up.push("lotsOfStuff")
		.each("The", "joy", "of king", "and", "retainers is,", "however,", "of", "short duration.");
	    long startNS = System.nanoTime();
	    mongoOp.updateFirst(q, up, LargeDoc.class);
	    long endNS   = System.nanoTime();
	    measuredTimes.add(endNS - startNS);
	}
	Collections.sort(measuredTimes);
	LongSummaryStatistics stats = new LongSummaryStatistics();
	for (long v : measuredTimes) {
	    stats.accept(v);
	}
	System.console().printf("Total   update via update execution time (ms): %d\n", stats.getSum() / 1000000);
	System.console().printf("Average update via update insert time (ms): %f\n", stats.getAverage() / 1000000.0);
	System.console().printf("Median  update via update insert time (ms): %f\n", measuredTimes.get(measuredTimes.size()/2) / 1000000.0);
    }
    
    private void runReadTests() {
	measuredTimes.clear();

	long startNS = System.nanoTime();
	List<LargeDoc> docs = largeDocs.findByTestStringField("f");
	for (LargeDoc doc : docs) {
	}
	long endNS = System.nanoTime();
	double timeInMS = (endNS - startNS) / 1000000.0;
	System.console().printf("Searching and iterating over %d elements took %f ms\n",
				docs.size(), timeInMS);
	System.console().printf("Average search/iteration time per element: %f ms\n", timeInMS / docs.size());

	measuredTimes.clear();
	startNS = System.nanoTime();
	List<SmallDoc> sdocs = smallDocs.findAll();
	for (SmallDoc doc : sdocs) {
	}
	endNS = System.nanoTime();
	timeInMS = (endNS - startNS) / 1000000.0;
	System.console().printf("Searching and iterating over %d small documents took %f ms\n",
				sdocs.size(), timeInMS);
	System.console().printf("Average search/iteration time per small document: %f ms\n", timeInMS / sdocs.size());
    }

    private void runDeleteTests() {
    }

    private LargeDocGenerator docgenL;
    private SmallDocGenerator docGenS;
    
    private ArrayList<Long> measuredTimes;
}
