package spring-tests;

import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
public class BasicMongoConfiguration extends AbstractMongoConfiguration {
    @Override
    public String getDatabaseName() {
	System.console().printf("Getting database name\n");
	return "SpringTestDB";
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception 
    {
	System.console().printf("Getting mongo connection\n");
        return new MongoClient("localhost" , 27017 );
    }
}
