package com.lonecppcoder.mongo_high_load;

import com.mongodb.MongoClient;

import java.lang.Runnable;
import java.lang.Thread;
import java.util.Vector;

class ChangeStreamRunner implements Runnable {
    public ChangeStreamRunner(MongoClient conn, String[] monitoredCollections, String[] updatedCollections) {
        client = conn;
        //toMonitor = new Vector<String>(monitoredCollections);
        monitors = new Vector<Thread>();
        for (int i = 0; i < monitoredCollections.length; i++) {
            monitors.add(new Thread(new ChangeStreamMonitor(client, monitoredCollections[i], updatedCollections[i])));
        }
    }

    public void run() {
        monitors.forEach(m -> m.start());
        monitors.forEach(m -> {
                try {
                    m.join();
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        
    }

    MongoClient    client;
    //Vector<String> toMonitor;
    Vector<Thread> monitors;
}
