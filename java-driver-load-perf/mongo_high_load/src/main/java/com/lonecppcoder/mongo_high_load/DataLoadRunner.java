package com.lonecppcoder.mongo_high_load;

import com.mongodb.MongoClient;

import org.bson.Document;

import java.lang.Runnable;
import java.lang.Thread;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;


class DataLoadRunner implements Runnable, ProfilePrinter {
    public class TestDoc {
        public String dbName;
        public String collName;
        //public Document docAsBson;
        public String docAsString;
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
                docInfo.docAsString = new String(Files.readAllBytes(Paths.get(documents[i])), Charset.defaultCharset());
            }
            catch (IOException e) {
                System.err.println(e);
                continue;
            }
            testDocuments[i] = docInfo;
        }
    }

    public void run() {
        loaders = new Thread[numParallel];
        runnables = new Runnable[numParallel];
        for (int i = 0; i < numParallel; i++) {
            runnables[i] = new DataLoader(conn, testDocuments);
            loaders[i] = new Thread(runnables[i]);
            loaders[i].start();
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException e) {
                ;
            }
        }

        for (int i = 0; i < numParallel; i++) {
            try {
                loaders[i].join();
            }
            catch (InterruptedException e) {
                    throw new RuntimeException(e);
            }
        };
    }

    public void printStats() {
        long inserted = 0L;
        for (int i = 0; i < runnables.length; i++) {
            inserted += ((PerfDataCollector)runnables[i]).getAndReset();
        };
        System.out.printf("%d documents loaded\n", inserted);
    }

    private MongoClient conn;
    private TestDoc[] testDocuments;
    private int numParallel;

    private Thread[] loaders;
    private Runnable[] runnables;
}
