package com.lonecppcoder.mongo_high_load;

import java.lang.Thread;
import java.lang.Runnable;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.*;

import org.bson.Document;

class SequenceNumberIncrementor implements Runnable {
    public SequenceNumberIncrementor(MongoClient conn, String seqNumCollection, String seqNumName) {
        mongoConn = conn;
        collDB = seqNumCollection.substring(0, seqNumCollection.indexOf('.'));
        collName = seqNumCollection.substring(seqNumCollection.indexOf('.') + 1);
        this.seqNumName = seqNumName;
    }

    public void run() {
        MongoCollection collection = mongoConn.getDatabase(collDB).getCollection(collName);

        Document filter = new Document("_id", seqNumName);
        Document update = new Document("$inc", new Document("sequence", 1));
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(true);

        try {
            while (true) {
                collection.findOneAndUpdate(filter, update, options);
                Thread.sleep(50);
            }
        }
        catch (InterruptedException e) {
            ;
        }
    }

    private MongoClient mongoConn;
    private String      collDB;
    private String      collName;
    private String      seqNumName;
}
