package com.lonecppcoder.mongo_high_load;

import com.mongodb.MongoClient;

import java.lang.Thread;
import java.util.Vector;

public class MainLoadTest
{
    public static void main( String[] args )
    {
        MongoClient client = new MongoClient();
        
        Vector<Thread> Runners = new Vector<Thread>();

        Runners.add(new Thread(new SequenceNumberRunner(new String[]{"inserted", "updated", "updated-again"}, client, "load_test.sequence_ids")));
        Runners.add(new Thread(new ChangeStreamRunner(client, new String[]{ "load_test.documents" }, new String[]{ "load_test.doc_resume" })));
        Runners.add(new Thread(new DataLoadRunner(client, new String[]{ "load_test.xml_docs.initial_load.json",
                                                                        "load_test.documents.first_transformation.json",
                                                                        "load_test.documents.second_transformation.json"}, 5)));

        Runners.forEach((r) -> r.start());

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
}
