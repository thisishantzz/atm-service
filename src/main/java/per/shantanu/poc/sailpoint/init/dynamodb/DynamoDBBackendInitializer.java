package per.shantanu.poc.sailpoint.init.dynamodb;

import lombok.Builder;
import per.shantanu.poc.sailpoint.init.Initializer;
import per.shantanu.poc.sailpoint.repositories.dynamodb.DynamoDBAccountRepository;
import per.shantanu.poc.sailpoint.repositories.dynamodb.DynamoDBCredentialsRepository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@Builder(builderMethodName = "create")
public class DynamoDBBackendInitializer implements Initializer {

  private final DynamoDbAsyncClient dynamodb;

  @Override
  @SuppressWarnings("unchecked")
  public Mono<Void> initialize() {
    return Mono.fromFuture(
            dynamodb.createTable(
                tbl ->
                    tbl.tableName(DynamoDBCredentialsRepository.TABLE_NAME)
                        .attributeDefinitions(
                            attr ->
                                attr.attributeName(DynamoDBCredentialsRepository.TABLE_KEY)
                                    .attributeType(ScalarAttributeType.S)
                                    .attributeType(DynamoDBCredentialsRepository.FIELD_PIN)
                                    .attributeType(ScalarAttributeType.S))
                        .keySchema(
                            key ->
                                key.attributeName(DynamoDBCredentialsRepository.TABLE_KEY)
                                    .keyType(KeyType.HASH))
                        .provisionedThroughput(
                            thp -> {
                              thp.readCapacityUnits(5L);
                              thp.writeCapacityUnits(5L);
                            })))
        .then(
            Mono.fromFuture(
                dynamodb.createTable(
                    tbl ->
                        tbl.tableName(DynamoDBAccountRepository.TABLE_NAME)
                            .attributeDefinitions(
                                attr ->
                                    attr.attributeName(DynamoDBAccountRepository.TABLE_KEY)
                                        .attributeType(ScalarAttributeType.S)
                                        .attributeName(DynamoDBAccountRepository.FIELD_CUSTOMER_ID)
                                        .attributeType(ScalarAttributeType.S)
                                        .attributeName(DynamoDBAccountRepository.FIELD_BALANCE)
                                        .attributeType(ScalarAttributeType.N))
                            .keySchema(
                                key ->
                                    key.attributeName(DynamoDBAccountRepository.TABLE_KEY)
                                        .keyType(KeyType.HASH))
                            .provisionedThroughput(
                                thp -> {
                                  thp.readCapacityUnits(5L);
                                  thp.writeCapacityUnits(5L);
                                }))))
        .then();
  }
}
