package spring_tests;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SmallDocRepository extends MongoRepository<SmallDoc, String> {

    public SmallDoc findByRatio(double r);
}
