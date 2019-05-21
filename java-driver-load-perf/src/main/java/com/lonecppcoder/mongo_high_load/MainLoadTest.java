package com.lonecppcoder.mongo_high_load;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.apache.commons.cli.*;

import java.lang.Thread;
import java.util.Vector;
import java.util.Arrays;

public class MainLoadTest
{
    public static void main( String[] args )
    {
        Option uri               = Option.builder("u").argName("uri").longOpt("uri").hasArg().desc("URI of MongoDB servers to connect to. Defaults to localhost:27017").build();
        Option files             = Option.builder("f").argName("files").longOpt("files").hasArgs().desc("Comma separated list of files to use in the load runner test").build();
        Option loaderThreads     = Option.builder("l").argName("loader-threads").longOpt("loader-threads").hasArg().desc("Number of parallel document loader threads to run").build();
        Option seqThreads        = Option.builder("s").argName("sequence-threads").longOpt("sequence-threads").hasArg().desc("Number of parallel seqence number incrememtor threads to run").build();
        Option flushAfterChanges = Option.builder("c").argName("flush-after-changes").longOpt("flush-after-changes").hasArg().desc("Number of change events received before flushing change stream resume token. Default 1").build();
        Option help              = Option.builder("h").argName("help").longOpt("help").desc("Print this message").build();
        Option monitorCollection = Option.builder("m").longOpt("monitor-collection").hasArg().desc("FQ name of the collection the changestream monitors").build();
        

        Options options = new Options().addOption(uri)
            .addOption(files)
            .addOption(loaderThreads)
            .addOption(seqThreads)
            .addOption(flushAfterChanges)
            .addOption(help)
            .addOption(monitorCollection);

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cli = parser.parse(options, args);

            if (cli.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("load-test", options);
                return;
            }
            
            String mongoURI = cli.hasOption("uri") ? cli.getOptionValue("uri") : "mongodb://localhost:27017";
            int    numLoaders = cli.hasOption("loader-threads") ? Integer.parseInt(cli.getOptionValue("loader-threads")) : 5;
            int    numFlushes = cli.hasOption("flush-after-changes") ? Integer.parseInt(cli.getOptionValue("flush-after-changes")) : 1;
            String[] testDocs = cli.hasOption("files")
                ? cli.getOptionValue("files").split(",")
                : new String[]{ "load_test.xml_docs.initial_load.json", "load_test.documents.first_transformation.json", "load_test.documents.second_transformation.json"};
            String[] monitorColls = cli.hasOption("m")
                ? cli.getOptionValue("m").split(",")
                : new String[]{ "load_test.documents" };

            System.out.printf("Using input document files %s\n", Arrays.toString(testDocs));
            
            running = true;
            MongoClient client = new MongoClient(new MongoClientURI(mongoURI));
        
            Vector<Thread> Runners = new Vector<Thread>();
            Vector<Runnable> Runnables = new Vector<Runnable>();

            Runnables.add(new SequenceNumberRunner(new String[]{"inserted", "updated", "updated-again"}, client, "load_test.sequence_ids"));
            Runnables.add(new ChangeStreamRunner(client, monitorColls, new String[]{ "load_test.doc_resume" }, numFlushes));
            Runnables.add(new DataLoadRunner(client, testDocs , numLoaders));

            for (int i = 0; i < Runnables.size(); i++) {
                Thread t = new Thread(Runnables.get(i));
                Runners.add(t);
                t.start();
            }

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
        catch (ParseException e) {
            System.err.printf("Parsing command line options failed with error %s\n", e.getMessage());
        }
    }

    static boolean running;
}
