package com.lonecppcoder.mongo_high_load;

import com.mongodb.MongoClient;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.changestream.ChangeStreamDocument;

import org.bson.Document;
import org.bson.BsonDocument;

import java.lang.Runnable;

class ChangeStreamMonitor implements Runnable {
    public ChangeStreamMonitor(MongoClient client, String fqFromColl, String fqToColl) {
        String dbName = fqFromColl.substring(0, fqFromColl.indexOf('.'));
        String collName = fqFromColl.substring(fqFromColl.indexOf('.') + 1);
        fromColl = client.getDatabase(dbName).getCollection(collName);
        dbName = fqToColl.substring(0, fqToColl.indexOf('.'));
        collName = fqToColl.substring(fqToColl.indexOf('.') + 1);
        toColl = client.getDatabase(dbName).getCollection(collName);
    }
    
    public void run() {
        Block<ChangeStreamDocument<Document>> updateBlock = new Block<ChangeStreamDocument<Document>>() {
                public void apply(final ChangeStreamDocument<Document> d) {
                    BsonDocument resumeToken = d.getResumeToken();
                    toColl.findOneAndUpdate(new Document("_id", 1), new Document("$set", new Document("resumeToken", resumeToken)), new FindOneAndUpdateOptions().upsert(true));
                }
            };
        
        fromColl.watch().forEach(updateBlock);
    }

    MongoCollection fromColl;
    MongoCollection toColl;
}
