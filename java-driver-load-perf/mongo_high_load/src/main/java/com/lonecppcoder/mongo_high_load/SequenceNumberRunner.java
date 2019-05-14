package com.lonecppcoder.mongo_high_load;

import java.lang.Thread;

import com.mongodb.MongoClient;

class SequenceNumberRunner {
    public SequenceNumberRunner(String[] sequenceNames, MongoClient conn, String fqCollName) {
        names = sequenceNames;
        numSeqNumbers = sequenceNames.length;

        client = conn;
        collName = fqCollName;
    }

    public void run() {
        Thread[] allSeqs = new Thread[numSeqNumbers];
        for (int i = 0; i < numSeqNumbers; i++) {
            allSeqs[i] = new Thread(new SequenceNumberIncrementor(client, collName, names[i]));
            allSeqs[i].start();
        }
        try {
            for (int i = 0; i < numSeqNumbers; i++) {
                allSeqs[i].join();
            }
        }
        catch (InterruptedException e) {
            ;
        }
    }

    private String[] names;
    private int numSeqNumbers;

    private MongoClient client;
    private String collName;
}
