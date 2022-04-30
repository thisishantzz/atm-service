package per.shantanu.poc.sailpoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "per.shantanu")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
