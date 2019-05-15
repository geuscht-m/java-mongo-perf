package com.lonecppcoder.mongo_high_load;

import com.mongodb.MongoClient;

import org.bson.Document;

import java.lang.Runnable;
import java.util.Vector;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;


class DataLoadRunner implements Runnable {
    public class TestDoc {
        public String dbName;
        public String collName;
        public Document docAsBson;
    }
    
    DataLoadRunner(MongoClient client, String[] documents, int parallelLoads) {
        numParallel = parallelLoads;
        conn = client;

        testDocuments = new TestDoc[documents.length];
        
        for (int i = 0; i < documents.length; i++) {
            String[] fNameParts = documents[i].split("\\.");
            TestDoc docInfo = new TestDoc();
            docInfo.dbName = fNameParts[0];
            docInfo.collName = fNameParts[1];
            try {
                docInfo.docAsBson = Document.parse(new String(Files.readAllBytes(Paths.get(fNameParts[2])), Charset.defaultCharset()));
            }
            catch (IOException e) {
                System.err.println(e);
                continue;
            }
            testDocuments[i] = docInfo;
        }
    }

    public void run() {
        Vector<Thread> loaders = new Vector<Thread>();
        for (int i = 0; i < numParallel; i++) {
            loaders.add(new Thread(new DataLoader(conn, testDocuments)));
        }
        loaders.forEach(l -> l.start());
        loaders.forEach(l -> {
                try {
                    l.join();
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

    }

    private MongoClient conn;
    private TestDoc[] testDocuments;
    private int numParallel;
}
