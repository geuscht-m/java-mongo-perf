package driver_tests;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
	TestRunner tr = new TestRunner();

	tr.initialiseTests();
	tr.runInsertTests();
	tr.runSingleDocumentUpdateTests();
	tr.dropTestCollections();
	tr.runBulkInsertTests();
    }
}
