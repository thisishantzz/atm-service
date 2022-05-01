package per.shantanu.poc.sailpoint.config;

import java.time.ZoneId;
import lombok.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfiguration {

  @Bean
  public WebFluxConfigurer webFluxConfigurer(ObjectProvider<CodecCustomizer> codecCustomizers) {
    return new WebFluxConfigurer() {
      @Override
      public void configureHttpMessageCodecs(@NonNull ServerCodecConfigurer configurer) {
        codecCustomizers.orderedStream().forEach(c -> c.customize(configurer));
      }
    };
  }

  @Bean
  @ConditionalOnMissingBean
  public ZoneId applicationTimezone(ApplicationProperties properties) {
    return properties.timezone();
  }
}
