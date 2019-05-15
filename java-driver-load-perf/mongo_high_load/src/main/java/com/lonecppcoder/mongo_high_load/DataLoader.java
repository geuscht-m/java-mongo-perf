package com.lonecppcoder.mongo_high_load;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import java.lang.Runnable;
import java.lang.Thread;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

class DataLoader implements Runnable, PerfDataCollector {
    DataLoader(MongoClient conn, DataLoadRunner.TestDoc[] testInfo) {
        client = conn;
        testData = testInfo;
    }

    public void run() {
        MongoCollection coll;
        try {
            for (int i = 0; i < testData.length; i++) {
                coll = client.getDatabase(testData[i].dbName).getCollection(testData[i].collName);
                coll.insertOne(testData[i].docAsBson);
                stats.incrementAndGet();
                Thread.sleep(ThreadLocalRandom.current().nextInt(30, 300));
            }
        }
        catch (InterruptedException e) {
            ;
        }
    }

    public AtomicLong getStat() {
        return stats;
    }

    private MongoClient client;
    private DataLoadRunner.TestDoc[] testData;
    private AtomicLong stats;
  
}
