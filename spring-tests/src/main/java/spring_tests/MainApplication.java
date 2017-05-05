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
	largeDocs.deleteAll();
	System.console().printf("Done initializing tests\n");
    }
    
    private void runInsertTests() {
	measuredTimes.clear();

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
	System.console().printf("Median insert time (ms): %d\n", measuredTimes.get(100001/2) / 1000);
    }

    private void runUpdateViaSaveTests() {
	measuredTimes.clear();
	
	System.console().printf("Begin update via save tests\n");
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
	System.console().printf("Median  update via save insert time (ms): %d\n", measuredTimes.get(measuredTimes.size()/2) / 1000);
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
	System.console().printf("Total   update via update execution time (ms): %d\n", stats.getSum() / 1000);
	System.console().printf("Average update via update insert time (ms): %f\n", stats.getAverage() / 1000);
	System.console().printf("Median  update via update insert time (ms): %d\n", measuredTimes.get(measuredTimes.size()/2) / 1000);
    }
    
    private void runReadTests() {
    }

    private void runDeleteTests() {
    }

    private LargeDocGenerator docgenL;
    private ArrayList<Long> measuredTimes;
}
