package spring_tests;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LargeDocRepository extends MongoRepository<LargeDoc, String> {
    public List<LargeDoc> findByTestStringField(String f);
}
