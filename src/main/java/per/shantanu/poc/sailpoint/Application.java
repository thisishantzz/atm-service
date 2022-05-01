package per.shantanu.poc.sailpoint;

import java.time.ZoneId;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import per.shantanu.poc.sailpoint.config.ApplicationProperties;
import per.shantanu.poc.sailpoint.init.Initializer;

@SpringBootApplication(scanBasePackages = "per.shantanu")
@RequiredArgsConstructor
public class Application {

  private final Initializer initializer;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public ZoneId timezone(@NonNull ApplicationProperties properties) {
    return properties.timezone();
  }
}
