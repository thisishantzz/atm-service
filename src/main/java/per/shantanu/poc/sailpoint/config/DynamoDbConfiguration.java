package per.shantanu.poc.sailpoint.config;

import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import per.shantanu.poc.sailpoint.repositories.AccountRepository;
import per.shantanu.poc.sailpoint.repositories.CredentialsRepository;
import per.shantanu.poc.sailpoint.repositories.dynamodb.DynamoDBAccountRepository;
import per.shantanu.poc.sailpoint.repositories.dynamodb.DynamoDBCredentialsRepository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = ApplicationProperties.PREFIX, name = "db", havingValue = "DYNAMODB")
@EnableConfigurationProperties(DynamodbProperties.class)
public class DynamoDbConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public AwsCredentialsProvider credentialsProvider(@NonNull DynamodbProperties properties) {
    return StaticCredentialsProvider.create(
        AwsBasicCredentials.create(properties.accessKey(), properties.secretKey()));
  }

  @Bean
  @ConditionalOnProperty(prefix = DynamodbProperties.PREFIX, name = "endpoint")
  public DynamoDbAsyncClient dynamoDbAsyncClient(
      @NonNull DynamodbProperties properties, AwsCredentialsProvider credentialsProvider) {
    return DynamoDbAsyncClient.builder()
        .region(properties.region())
        .credentialsProvider(credentialsProvider)
        .httpClientBuilder(NettyNioAsyncHttpClient.builder())
        .endpointOverride(properties.endpoint())
        .build();
  }

  @Bean
  @ConditionalOnMissingBean
  public DynamoDbAsyncClient defaultDynamoDbAsyncClient(
      @NonNull DynamodbProperties properties, AwsCredentialsProvider credentialsProvider) {
    return DynamoDbAsyncClient.builder()
        .region(properties.region())
        .credentialsProvider(credentialsProvider)
        .httpClientBuilder(NettyNioAsyncHttpClient.builder())
        .build();
  }

  @Bean
  public CredentialsRepository credentialsRepository(DynamoDbAsyncClient client) {
    return DynamoDBCredentialsRepository.create().dynamodb(client).build();
  }

  @Bean
  public AccountRepository accountRepository(DynamoDbAsyncClient client) {
    return DynamoDBAccountRepository.create().dynamodb(client).build();
  }
}
