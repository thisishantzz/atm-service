package per.shantanu.poc.atm.config;

import java.time.ZoneId;
import lombok.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import per.shantanu.poc.atm.entities.Account;
import per.shantanu.poc.atm.entities.Credentials;
import per.shantanu.poc.atm.handlers.ATMServiceController;
import per.shantanu.poc.atm.internal.jackson.AccountSerializer;
import per.shantanu.poc.atm.internal.jackson.AmountDeserializer;
import per.shantanu.poc.atm.internal.jackson.CredentialDeserializer;

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

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer enhanceObjectMapper() {
    return builder -> {
      builder.deserializerByType(Credentials.class, new CredentialDeserializer());
      builder.deserializerByType(ATMServiceController.Amount.class, new AmountDeserializer());
      builder.serializerByType(Account.class, new AccountSerializer());
    };
  }
}
