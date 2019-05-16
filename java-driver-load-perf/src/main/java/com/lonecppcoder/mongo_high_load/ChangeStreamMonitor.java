package com.lonecppcoder.mongo_high_load;

import com.mongodb.MongoClient;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.changestream.ChangeStreamDocument;

import org.bson.Document;
import org.bson.BsonDocument;

import java.lang.Runnable;
import java.util.concurrent.atomic.AtomicLong;

class ChangeStreamMonitor implements Runnable, PerfDataCollector {
    public ChangeStreamMonitor(MongoClient client, String fqFromColl, String fqToColl) {
        stats = new AtomicLong(0L);
        String dbName = fqFromColl.substring(0, fqFromColl.indexOf('.'));
        String collName = fqFromColl.substring(fqFromColl.indexOf('.') + 1);
        fromColl = client.getDatabase(dbName).getCollection(collName);
        dbName = fqToColl.substring(0, fqToColl.indexOf('.'));
        collName = fqToColl.substring(fqToColl.indexOf('.') + 1);
        toColl = client.getDatabase(dbName).getCollection(collName);
    }
    
    public void run() {
        final FindOneAndUpdateOptions opts = new FindOneAndUpdateOptions().upsert(true);
        final Document findCriteria = new Document("_id", 1);
        Block<ChangeStreamDocument<Document>> updateBlock = new Block<ChangeStreamDocument<Document>>() {
                @Override
                public void apply(final ChangeStreamDocument<Document> d) {
                    BsonDocument resumeToken = d.getResumeToken();
                    toColl.findOneAndUpdate(findCriteria, new Document("$set", new Document("resumeToken", resumeToken)), opts);
                    stats.incrementAndGet();
                }
            };
        
        fromColl.watch().forEach(updateBlock);
    }

    public AtomicLong getStat() {
        return stats;
    }

    private MongoCollection fromColl;
    private MongoCollection toColl;

    private AtomicLong stats;
}
