package per.shantanu.poc.sailpoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import per.shantanu.poc.sailpoint.config.ApplicationProperties;

@SpringBootApplication(scanBasePackages = "per.shantanu")
@EnableConfigurationProperties(ApplicationProperties.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
