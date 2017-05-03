package spring_tests;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface LargeDocRepository extends MongoRepository<LargeDoc, String> {
    public LargeDoc findByTestStringField(String f);
}
