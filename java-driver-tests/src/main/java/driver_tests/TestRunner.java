package driver_tests;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.BasicDBObject;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;

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

    public void initialiseTests() {
	MongoCollection<Document> smallDocs = db.getCollection("smallDocs");
	smallDocs.drop();
	MongoCollection<Document> largeDocs = db.getCollection("largeDocs");
	largeDocs.deleteMany(new BasicDBObject());
	largeDocs.createIndex(Indexes.ascending("testStringField"));
    }

    public void runInsertTests() {
	measuredTimes.clear();
	
	MongoCollection<Document> ldc = db.getCollection("largeDocs");
	
	for (int i = 0; i < LARGE_DOC_TEST_SET_SIZE; i++) {
	    Document doc = largeDocs.generateNextDocument();
	    long startNS = System.nanoTime();
	    ldc.insertOne(doc);
	    long endNS = System.nanoTime();
	    measuredTimes.add(endNS - startNS);
	}

	Collections.sort(measuredTimes);
	LongSummaryStatistics stats = new LongSummaryStatistics();
	for (long v : measuredTimes) {
	    stats.accept(v);
	}
	System.console().printf("Large Documents: Total insert execution time (ms) : %d\n", stats.getSum() / 1000);
	System.console().printf("Large Documents: Average insert time (ms):   %f\n", stats.getAverage() / 1000);
	System.console().printf("Large Docuemtns: Median insert time (ms):    %d\n", measuredTimes.get(LARGE_DOC_TEST_SET_SIZE/2) / 1000);

	measuredTimes.clear();

	MongoCollection<Document> sdc = db.getCollection("smallDocs");
	
	for (int i = 0; i < SMALL_DOC_TEST_SET_SIZE; i++) {
	    Document doc = smallDocs.generateNextDocument();
	    long startNS = System.nanoTime();
	    sdc.insertOne(doc);
	    long endNS = System.nanoTime();
	    measuredTimes.add(endNS - startNS);
	}

	Collections.sort(measuredTimes);
	stats = new LongSummaryStatistics();
	for (long v : measuredTimes) {
	    stats.accept(v);
	}
	System.console().printf("Small Documents: Total insert execution time (ms) : %d\n", stats.getSum() / 1000);
	System.console().printf("Small Documents: Average insert time (ms):   %f\n", stats.getAverage() / 1000);
	System.console().printf("Small Documents: Median insert time (ms):    %d\n", measuredTimes.get(SMALL_DOC_TEST_SET_SIZE/2) / 1000);
    }

    public void runUpdateTests() {
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
