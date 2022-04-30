package per.shantanu.poc.sailpoint;

import lombok.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import per.shantanu.poc.sailpoint.config.ApplicationProperties;

import java.time.ZoneId;

@SpringBootApplication(scanBasePackages = "per.shantanu")
@EnableConfigurationProperties(ApplicationProperties.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ZoneId timezone(@NonNull ApplicationProperties properties) {
        return properties.timezone();
    }
}
