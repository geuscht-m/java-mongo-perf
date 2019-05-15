package com.lonecppcoder.mongo_high_load;


import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.*;

import org.bson.Document;

import java.lang.Thread;
import java.lang.Runnable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

class SequenceNumberIncrementor implements Runnable, PerfDataCollector {
    public SequenceNumberIncrementor(MongoClient conn, String seqNumCollection, String seqNumName) {
        mongoConn = conn;
        collDB = seqNumCollection.substring(0, seqNumCollection.indexOf('.'));
        collName = seqNumCollection.substring(seqNumCollection.indexOf('.') + 1);
        this.seqNumName = seqNumName;
        this.stats = new AtomicLong();
    }

    public void run() {
        MongoCollection collection = mongoConn.getDatabase(collDB).getCollection(collName);

        Document filter = new Document("_id", seqNumName);
        Document update = new Document("$inc", new Document("sequence", 1));
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(true);

        //try {
            while (true) {
                collection.findOneAndUpdate(filter, update, options);
                stats.incrementAndGet();
                //Thread.sleep(ThreadLocalRandom.current().nextInt(7, 45));
            }
            //}
        //catch (InterruptedException e) {
        //    ;
        //}
    }

    public AtomicLong getStat() {
        return stats;
    }

    private MongoClient mongoConn;
    private String      collDB;
    private String      collName;
    private String      seqNumName;

    private AtomicLong  stats;
}
