package driver_tests;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.BasicDBObject;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.model.InsertOneModel;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LongSummaryStatistics;


public class TestRunner {
    public TestRunner() throws IOException {
	client = new MongoClient();
	db     = client.getDatabase("driver_test");

	largeDocs = new LargeDocumentGenerator("../spring-tests/metamorphosis-kafka.txt");
	smallDocs = new SmallDocumentGenerator("../spring-tests/huck-finn.txt");
	
	measuredTimes = new ArrayList<Long>(200001);
    }

    public void dropTestCollections() {
	MongoCollection<Document> smallDocs = db.getCollection("smallDocs");
	smallDocs.drop();
	MongoCollection<Document> largeDocs = db.getCollection("largeDocs");
	largeDocs.drop();
    }
    
    public void initialiseTests() {
	dropTestCollections();
	MongoCollection<Document> largeDocs = db.getCollection("largeDocs");
	largeDocs.createIndex(Indexes.ascending("testStringField"));
    }

    private LongSummaryStatistics prepMeasuredTimes() {
	Collections.sort(measuredTimes);
	LongSummaryStatistics stats = new LongSummaryStatistics();
	for (long v : measuredTimes) {
	    stats.accept(v);
	}
	return stats;	
    }
    
    private <T extends DocumentGenerator> LongSummaryStatistics runSingleInsertTest(T      docGen,
										    String collectionName,
										    int    testSize) {
	measuredTimes.clear();
	
	MongoCollection<Document> ldc = db.getCollection(collectionName);
	
	for (int i = 0; i < testSize; i++) {
	    Document doc = docGen.generateNextDocument();
	    long startNS = System.nanoTime();
	    ldc.insertOne(doc);
	    long endNS = System.nanoTime();
	    measuredTimes.add(endNS - startNS);
	}
	
	return prepMeasuredTimes();
    }

    private void logSingleInsertTestResults(String messagePrefix, LongSummaryStatistics stats, ArrayList<Long> measuredTimes, int sampleSize) {
	System.console().printf("%s: Total insert execution time (ms) : %d\n", messagePrefix, stats.getSum() / 1000);
	System.console().printf("%s: Average insert time (ms):          %f\n", messagePrefix, stats.getAverage() / 1000);
	System.console().printf("%s: Median insert time (ms):           %d\n", messagePrefix, measuredTimes.get(sampleSize/2) / 1000);
    }
    
    public void runInsertTests() {
	System.console().printf("Starting large document insert tests\n");

	LongSummaryStatistics stats = runSingleInsertTest(largeDocs, "largeDocs", LARGE_DOC_TEST_SET_SIZE);

	logSingleInsertTestResults("Large Documents", stats, measuredTimes, LARGE_DOC_TEST_SET_SIZE);

	System.console().printf("Starting small document insert tests\n");
	measuredTimes.clear();

	stats = runSingleInsertTest(smallDocs, "smallDocs", SMALL_DOC_TEST_SET_SIZE);

	logSingleInsertTestResults("Small Documents", stats, measuredTimes, SMALL_DOC_TEST_SET_SIZE);
    }

    private <T extends DocumentGenerator> LongSummaryStatistics runSingleBulkInsertTest(T docGen, String collName) {
	measuredTimes.clear();

	MongoCollection<Document> dc = db.getCollection(collName);

	for (int i = 0; i < LARGE_DOC_TEST_SET_SIZE / 1000; i++) {
	    ArrayList<WriteModel<Document> > docs = new ArrayList();
	    for (int j = 0; j < 1000; j++) {
		docs.add(new InsertOneModel<>(docGen.generateNextDocument()));
	    }
	    long startNS = System.nanoTime();
	    dc.bulkWrite(docs);
	    long endNS = System.nanoTime();
	    measuredTimes.add(endNS - startNS);
	}
	Collections.sort(measuredTimes);
	LongSummaryStatistics stats = new LongSummaryStatistics();
	for (long v : measuredTimes) {
	    stats.accept(v);
	}
	
	return stats;
    }

    private void logBulkInsertTestResults(String prefix, LongSummaryStatistics stats, ArrayList<Long> measuredTimes) {
	System.console().printf("%s: Total bulk insert execution time (ms) : %d\n", prefix, stats.getSum() / 1000);
	System.console().printf("%s: Average bulk insert time per 1000 (ms): %f, average per document (ms): %f\n", prefix, stats.getAverage() / 1000, (stats.getAverage() / (1000 * 1000)));
	System.console().printf("%s: Median bulk insert time per 1000 (ms):  %d\n", prefix, measuredTimes.get(measuredTimes.size()/2) / 1000);
    }
    
    public void runBulkInsertTests() {
	System.console().printf("Starting large document bulk insert tests\n");
	LongSummaryStatistics stats = runSingleBulkInsertTest(largeDocs, "largeDocs");

	logBulkInsertTestResults("Large Documents", stats, measuredTimes);

	System.console().printf("Starting small document bulk insert tests\n");
	stats = runSingleBulkInsertTest(smallDocs, "smallDocs");

	logBulkInsertTestResults("Small Documents", stats, measuredTimes);
    }

    private static void logSingleDocUpdateTestResults(String messagePrefix, LongSummaryStatistics stats, ArrayList<Long> measuredTimes) {
	System.console().printf("%s: Total single document update execution time (ms) : %d\n", messagePrefix, stats.getSum() / 1000);
	System.console().printf("%s: Average single document update time (ms):          %f\n", messagePrefix, stats.getAverage() / 1000);
	System.console().printf("%s: Median single document update time (ms):           %d\n", messagePrefix, measuredTimes.get(measuredTimes.size() / 2) / 1000);
    }
	
    public void runSingleDocumentUpdateTests() {
	measuredTimes.clear();
	System.console().printf("Starting large document single document update tests\n");

	MongoCollection ldocs = db.getCollection("largeDocs");
	
	MongoCursor<Document> it = ldocs.find(eq("testStringField", "a")).iterator();

	while (it.hasNext()) {
	    Document doc = it.next();

	    long startNS = System.nanoTime();
	    ldocs.updateOne(eq("_id", doc.getObjectId("_id")),
			    combine(push("lotsOfStuff", "or palace,"),
				    push("lotsOfStuff", "in which he hopes"),
				    push("lotsOfStuff","to"),
				    push("lotsOfStuff", "feast his liegemen"),
				    push("lotsOfStuff", "and"),
				    push("lotsOfStuff", "to"),
				    push("lotsOfStuff", "give"),
				    push("lotsOfStuff", "them"),
				    push("lotsOfStuff", "presents.")));
	    long endNS = System.nanoTime();
	    measuredTimes.add(endNS - startNS);
	}

	LongSummaryStatistics stats = prepMeasuredTimes();
	logSingleDocUpdateTestResults("Large Document Update Test", stats, measuredTimes);

	measuredTimes.clear();
	System.console().printf("Starting small document single document update tests\n");

	MongoCollection sdocs = db.getCollection("smallDocs");
	it = sdocs.find().iterator();

	while (it.hasNext()) {
	    Document doc = it.next();
	    long startNS = System.nanoTime();
	    sdocs.updateOne(eq("_id", doc.getObjectId("_id")),
			    set("frontSpring", doc.getString("rearSpring")));
	    long endNS = System.nanoTime();
	    measuredTimes.add(endNS - startNS);
	}
	stats = prepMeasuredTimes();
	logSingleDocUpdateTestResults("Small Document Update Test", stats, measuredTimes);
    }

    private static void logBulkDocUpdateTestResults(String messagePrefix, long timeElapsed, int numDocs) {
	long timeElapsedMS = timeElapsed / 1000;
	System.console().printf("%s: Total bulk document update execution time (ms): %d\n", messagePrefix, timeElapsedMS);
	System.console().printf("%s: Average bulk document update time (ms):         %d\n", messagePrefix, timeElapsedMS / numDocs);
	//System.console().printf("%s: Median single document update time (ms):         %d\n", messagePrefix, measuredTimes.get(measuredTimes.size() / 2) / 1000);
    }

    public void runBulkDocumentUpdateTests() {
	System.console().printf("Starting large document bulk document update tests\n");

	MongoCollection ldocs = db.getCollection("largeDocs");
	
	long startNS = System.nanoTime();
	ldocs.updateMany(eq("testStringField", "a"),
			    combine(push("lotsOfStuff", "or palace,"),
				    push("lotsOfStuff", "in which he hopes"),
				    push("lotsOfStuff","to"),
				    push("lotsOfStuff", "feast his liegemen"),
				    push("lotsOfStuff", "and"),
				    push("lotsOfStuff", "to"),
				    push("lotsOfStuff", "give"),
				    push("lotsOfStuff", "them"),
				    push("lotsOfStuff", "presents.")));
	long endNS = System.nanoTime();
	
	LongSummaryStatistics stats = prepMeasuredTimes();
	logBulkDocUpdateTestResults("Large Document Bulk Update Test", endNS - startNS, LARGE_DOC_TEST_SET_SIZE);
    }	

    public void runDeleteTests() {
    }
    
    private MongoClient   client;
    private MongoDatabase db;

    private ArrayList<Long> measuredTimes;

    private LargeDocumentGenerator largeDocs;
    private SmallDocumentGenerator smallDocs;

    final int LARGE_DOC_TEST_SET_SIZE = 100001;
    final int SMALL_DOC_TEST_SET_SIZE = 200001;
}
