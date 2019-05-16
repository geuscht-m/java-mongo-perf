package com.lonecppcoder.mongo_high_load;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import org.bson.Document;

import java.lang.Runnable;
import java.lang.Thread;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

class DataLoader implements Runnable, PerfDataCollector {
    class TestData {
        public String dbName;
        public String collName;
        public Document docAsBson;

        TestData(DataLoadRunner.TestDoc doc) {
            dbName   = doc.dbName;
            collName = doc.collName;
            docAsBson =  Document.parse(doc.docAsString);
        }
    };
    
    DataLoader(MongoClient conn, DataLoadRunner.TestDoc[] testInfo) {
        stats = new AtomicLong(0L);
        client = conn;
        //testData = testInfo;
        testData = new TestData[testInfo.length];
        for (int i = 0; i < testInfo.length; i++) {
            testData[i] = new TestData(testInfo[i]);
        }
    }

    public void run() {
        //try {
            //MongoCollection coll;

            while (true) {
            //Thread.sleep(ThreadLocalRandom.current().nextInt(30, 600)); // Workaround for objectid generation
            for (int i = 0; i < testData.length; i++) {
                //System.out.printf("Starting to insert into collection %s in database %s\n", testData[i].collName, testData[i].dbName);
                MongoCollection coll = client.getDatabase(testData[i].dbName).getCollection(testData[i].collName);
                Document insertMe = testData[i].docAsBson;
                insertMe.remove("_id");
                insertMe.append("insert-info", new Document("thread", Thread.currentThread().getId()).append("counter", ++docCounter));
                coll.insertOne(testData[i].docAsBson);
                //System.out.println("Finished inserting document");
                stats.incrementAndGet();
                //Thread.sleep(ThreadLocalRandom.current().nextInt(30, 300));
            }
            }
            //}
            //catch (InterruptedException e) {
            //;
            //}
    }

    public AtomicLong getStat() {
        return stats;
    }

    private MongoClient client;
    private TestData[] testData;
    private AtomicLong stats;
    private long docCounter;
  
}
