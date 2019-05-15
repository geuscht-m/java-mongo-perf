package com.lonecppcoder.mongo_high_load;

import com.mongodb.MongoClient;

import java.lang.Thread;
import java.util.Vector;

public class MainLoadTest
{
    public static void main( String[] args )
    {
        running = true;
        MongoClient client = new MongoClient();
        
        Vector<Thread> Runners = new Vector<Thread>();
        Vector<Runnable> Runnables = new Vector<Runnable>();

        Runnables.add(new SequenceNumberRunner(new String[]{"inserted", "updated", "updated-again"}, client, "load_test.sequence_ids"));
        Runnables.add(new ChangeStreamRunner(client, new String[]{ "load_test.documents" }, new String[]{ "load_test.doc_resume" }));
        /*Runners.add(new Thread(new DataLoadRunner(client, new String[]{ "load_test.xml_docs.initial_load.json",
                                                                        "load_test.documents.first_transformation.json",
                                                                        "load_test.documents.second_transformation.json"}, 5)));*/

        for (int i = 0; i < Runnables.size(); i++) {
            Runners.add(new Thread(Runnables.get(i)));
        }
        
        Runners.forEach((r) -> r.start());

        try {
            while (running) {            
                Thread.sleep(10000);
                Runnables.forEach(r -> ((ProfilePrinter)r).printStats());
            }
        }
        catch (InterruptedException ex) {
            ;
        }

        try {
            Runners.forEach(r -> {
                    try {
                        r.join();
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
        }
        catch (RuntimeException e) {
            ;
        }
    }

    static boolean running;
}
