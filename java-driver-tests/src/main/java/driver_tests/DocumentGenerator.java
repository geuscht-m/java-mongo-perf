package driver_tests;

import org.bson.Document;

public interface DocumentGenerator {
  Document generateNextDocument();
}
