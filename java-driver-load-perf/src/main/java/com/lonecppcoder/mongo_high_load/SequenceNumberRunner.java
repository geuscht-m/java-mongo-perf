package com.lonecppcoder.mongo_high_load;

import java.lang.Thread;
import java.lang.Runnable;

import com.mongodb.MongoClient;

class SequenceNumberRunner implements Runnable, ProfilePrinter {
    public SequenceNumberRunner(String[] sequenceNames, MongoClient conn, String fqCollName) {
        names = sequenceNames;
        numSeqNumbers = sequenceNames.length;

        client = conn;
        collName = fqCollName;
    }

    public void run() {
        allSeqs = new Runnable[numSeqNumbers];
        allThreads = new Thread[numSeqNumbers];
        
        for (int i = 0; i < numSeqNumbers; i++) {
            allSeqs[i] = new SequenceNumberIncrementor(client, collName, names[i]);
            allThreads[i] = new Thread(allSeqs[i]);
            allThreads[i].start();
        }
        try {
            for (int i = 0; i < numSeqNumbers; i++) {
                allThreads[i].join();
            }
        }
        catch (InterruptedException e) {
            ;
        }
    }

    public void printStats() {
        long updates = 0;
        for (int i = 0; i < allSeqs.length; i++) {
            updates += ((PerfDataCollector)allSeqs[i]).getAndReset();
        }
        System.out.printf("%d sequence numbers Updated\n", updates);
    }

    private String[] names;
    private int numSeqNumbers;

    private Runnable[] allSeqs;
    private Thread[] allThreads;

    private MongoClient client;
    private String collName;
}
