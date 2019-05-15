package com.lonecppcoder.mongo_high_load;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import java.lang.Runnable;
import java.lang.Thread;
import java.util.concurrent.ThreadLocalRandom;

class DataLoader implements Runnable {
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
                Thread.sleep(ThreadLocalRandom.current().nextInt(30, 300));
            }
        }
        catch (InterruptedException e) {
            ;
        }
    }

    MongoClient client;
    DataLoadRunner.TestDoc[] testData;
}
