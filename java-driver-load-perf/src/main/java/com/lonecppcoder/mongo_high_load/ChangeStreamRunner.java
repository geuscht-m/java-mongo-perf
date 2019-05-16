package com.lonecppcoder.mongo_high_load;

import com.mongodb.MongoClient;

import java.lang.Runnable;
import java.lang.Thread;

class ChangeStreamRunner implements Runnable, ProfilePrinter {
    public ChangeStreamRunner(MongoClient conn, String[] monitoredCollections, String[] updatedCollections) {
        client = conn;
        monitors = new Thread[monitoredCollections.length];
        runnables = new Runnable[monitoredCollections.length];
        
        for (int i = 0; i < monitoredCollections.length; i++) {
            runnables[i] = new ChangeStreamMonitor(client, monitoredCollections[i], updatedCollections[i]);
            monitors[i] = new Thread(runnables[i], "ChangeStreamMonitor");
        }
    }

    public void run() {
        for (int i = 0; i < monitors.length; i++) {
            monitors[i].start();
        }
        for (int i = 0; i < monitors.length; i++) {
            try {
                monitors[i].join();
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        
    }

    public void printStats() {
        long updated = 0L;
        for (int i = 0; i < runnables.length; i++) {
            updated += ((PerfDataCollector)runnables[i]).getAndReset();
        }
        System.out.printf("%d resume tokens updated\n", updated);
    }

    MongoClient client;
    Thread[]    monitors;
    Runnable[]  runnables;
}
