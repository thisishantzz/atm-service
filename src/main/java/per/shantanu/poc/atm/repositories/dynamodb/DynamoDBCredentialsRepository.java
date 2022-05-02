package per.shantanu.poc.atm.repositories.dynamodb;

import java.util.Map;
import lombok.Builder;
import per.shantanu.poc.atm.entities.Credentials;
import per.shantanu.poc.atm.repositories.CredentialsRepository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

@Builder(builderMethodName = "create")
public class DynamoDBCredentialsRepository extends CredentialsRepository {

  public static final String TABLE_NAME = "Credentials";
  public static final String TABLE_KEY = "customerID";

  public static final String FIELD_PIN = "pin";

  private final DynamoDbAsyncClient dynamodb;

  @Override
  protected Mono<Credentials> findCredentialsInDB(String customerID) {

    return Mono.fromFuture(
            dynamodb.getItem(
                GetItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(Map.of(TABLE_KEY, AttributeValue.builder().s(customerID).build()))
                    .build()))
        .flatMap(
            t -> {
              if (t.item().size() <= 0) {
                return Mono.empty();
              }

              return Mono.fromCallable(
                  () -> {
                    final Map<String, AttributeValue> item = t.item();
                    return Credentials.create(item.get(TABLE_KEY).s(), item.get(FIELD_PIN).s());
                  });
            });
  }

  @Override
  protected Mono<Credentials> createCredentialInDB(Credentials credentials) {
    return Mono.fromFuture(
            dynamodb.putItem(
                b -> {
                  b.tableName(TABLE_NAME);
                  b.item(
                      Map.of(
                          TABLE_KEY,
                          AttributeValue.builder().s(credentials.customerID()).build(),
                          FIELD_PIN,
                          AttributeValue.builder().s(credentials.pin()).build()));
                }))
        .then(Mono.just(credentials));
  }
}
