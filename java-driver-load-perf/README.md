# Demo code showing the interaction between document insertion and downstream queueing using a changestream

## How to build the binary

The build system uses Maven with a few plugins. To build the performance testing tool, use the following command:

`mvn clean compile assembly:single`

This will create a single JAR containing the test code and all its dependencies and place it in the target folder. The filename of the target jar has the following format:

`mongo_high_load-<version>-SNAPSHOT-jar-with-dependencies.jar`

## Usage:

The performance tester has a couple of parameters that allow changing the overall performance envelope of the software. The parameters fall into these categories:

- Data files - controls the size and number of documents loaded for each iteration. Please note the files are read from disk only once and then cached in memory
- Threading controls - set the number of parallel data file loader and sequence number generator threads
- Resume token flush interval - set the number of changestream events the change stream processes before flushing the resume token to the database

The full list of support command line option can be displayed by running the jar with `-h` or `--help`:

```
usage: load-test
 -c,--flush-after-changes <flush-after-changes>   Number of change events
                                                  received before flushing
                                                  change stream resume
                                                  token. Default 1
 -f,--files <files>                               Comma separated list of
                                                  files to use in the load
                                                  runner test
 -h,--help                                        Print this message
 -l,--loader-threads <loader-threads>             Number of parallel
                                                  document loader threads
                                                  to run
 -m,--monitor-collection <arg>                    FQ name of the
                                                  collection the
                                                  changestream monitors
 -s,--sequence-threads <sequence-threads>         Number of parallel
                                                  seqence number
                                                  incrememtor threads to
                                                  run
 -u,--uri <uri>                                   URI of MongoDB servers
                                                  to connect to. Defaults
                                                  to localhost:27017
```

### Data files

The test code requires a list of files to use in its data loader threads. The filenames also encode the the name of the database and collection the data gets written into and _must_ be of the format <database-name>.<collection-name>.<name>.json and _must_ contain valid JSON.

Please note that at least one file's <database-name>.<collection-name> must match the collection that the changestream watches.

### Threading controls

You can control the number of parallel loader threads using the `-l` parameter. Each thread will insert new copies of the data files sequentially.

You can also control the number of threads that update sequences numbers using the parameter `-s`.

## TODO:

- ~~Current test document filenames are hardcoded~~
- ~~hardcoded of document loader threads need to be configurable~~
- ~~Number of sequence update threads need to be configurable~~
- Changestream code needs some experimental code to improve/work around performance as demo
