
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication implements CommandLineRunner {

    //@Autowired
    //private CustomerRepository repository;

    public static void main(String[] args) {
	SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
	runInsertTests();
	runUpdateTests();
	runReadTests();
	runDeleteTests();
    }

    private void runInsertTests() {
    }

    private void runUpdateTests() {
    }

    private void runReadTests() {
    }

    private void runDeleteTests() {
    }
}
