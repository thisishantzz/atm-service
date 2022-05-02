package per.shantanu.poc.atm.repositories.dynamodb;

import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import per.shantanu.poc.atm.entities.Account;
import per.shantanu.poc.atm.repositories.AccountRepository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@Builder(builderMethodName = "create")
public class DynamoDBAccountRepository extends AccountRepository {

  public static final String TABLE_NAME = "Account";
  public static final String TABLE_KEY = "accountNumber";

  public static final String FIELD_CUSTOMER_ID = "customerID";
  public static final String FIELD_BALANCE = "balance";

  private final DynamoDbAsyncClient dynamodb;

  @Override
  protected Mono<Account> findAccountInDB(String accountNumber) {

    return Mono.fromFuture(
            dynamodb.getItem(
                GetItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(Map.of(TABLE_KEY, AttributeValue.builder().s(accountNumber).build()))
                    .build()))
        .flatMap(
            t -> {
              if (t.item().size() <= 0) {
                return Mono.empty();
              }

              return Mono.fromCallable(
                  () -> {
                    final Map<String, AttributeValue> item = t.item();
                    return Account.create(
                        b -> {
                          b.accountNumber(accountNumber);
                          b.customerID(item.get(FIELD_CUSTOMER_ID).s());
                          b.balance(Double.parseDouble(item.get(FIELD_BALANCE).n()));
                        });
                  });
            });
  }

  @Override
  protected Mono<Account> createAccountInDB(Account account) {
    final String accountNumber = UUID.randomUUID().toString();

    return Mono.fromFuture(
            dynamodb.putItem(
                b -> {
                  b.tableName(TABLE_NAME);
                  b.item(
                      Map.of(
                          TABLE_KEY,
                          AttributeValue.builder().s(accountNumber).build(),
                          FIELD_CUSTOMER_ID,
                          AttributeValue.builder().s(account.customerID()).build(),
                          FIELD_BALANCE,
                          AttributeValue.builder().n(Double.toString(account.balance())).build()));
                }))
        .flatMap(
            t ->
                Mono.fromCallable(
                    () ->
                        Account.create(
                            b -> {
                              b.accountNumber(accountNumber);
                              b.customerID(account.customerID());
                              b.balance(account.balance());
                            })));
  }

  @Override
  protected Mono<Void> updateAccountInDB(Account account) {

    return Mono.fromFuture(
            dynamodb.putItem(
                PutItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .item(
                        Map.of(
                            TABLE_KEY,
                            AttributeValue.builder().s(account.accountNumber()).build(),
                            FIELD_CUSTOMER_ID,
                            AttributeValue.builder().s(account.customerID()).build(),
                            FIELD_BALANCE,
                            AttributeValue.builder().n(Double.toString(account.balance())).build()))
                    .build()))
        .then();
  }
}
