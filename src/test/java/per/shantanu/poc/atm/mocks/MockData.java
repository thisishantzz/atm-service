package per.shantanu.poc.atm.mocks;

import static org.mockito.Mockito.doReturn;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.mockito.ArgumentMatchers;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

public class MockData {

  public static final Consumer<DynamoDbAsyncClient> DYNAMODB_MOCKER =
      client -> {
        doReturn(
                CompletableFuture.supplyAsync(
                    () ->
                        GetItemResponse.builder()
                            .item(
                                Map.of(
                                    "customerID",
                                    AttributeValue.builder().s("test1").build(),
                                    "pin",
                                    AttributeValue.builder().s("1234").build()))
                            .build()))
            .when(client)
            .getItem(
                ArgumentMatchers.<GetItemRequest>argThat(
                    arg ->
                        Objects.equals(arg.tableName(), "Credentials")
                            && Objects.equals(arg.key().get("customerID").s(), "test1")));

        doReturn(
                CompletableFuture.supplyAsync(
                    () ->
                        GetItemResponse.builder()
                            .consumedCapacity(
                                b -> {
                                  b.readCapacityUnits(1D);
                                  b.writeCapacityUnits(1D);
                                })
                            .build()))
            .when(client)
            .getItem(
                ArgumentMatchers.<GetItemRequest>argThat(
                    arg ->
                        Objects.equals(arg.tableName(), "Credentials")
                            && Objects.equals(arg.key().get("customerID").s(), "wrong1")));

        doReturn(
                CompletableFuture.supplyAsync(
                    () ->
                        GetItemResponse.builder()
                            .item(
                                Map.of(
                                    "accountNumber",
                                    AttributeValue.builder().s("110").build(),
                                    "customerID",
                                    AttributeValue.builder().s("test1").build(),
                                    "balance",
                                    AttributeValue.builder().n("1000").build()))
                            .build()))
            .when(client)
            .getItem(
                ArgumentMatchers.<GetItemRequest>argThat(
                    arg ->
                        Objects.equals(arg.tableName(), "Account")
                            && Objects.equals(arg.key().get("accountNumber").s(), "110")));

        doReturn(
                CompletableFuture.supplyAsync(
                    () ->
                        GetItemResponse.builder()
                            .consumedCapacity(
                                b -> {
                                  b.readCapacityUnits(5D);
                                  b.writeCapacityUnits(5D);
                                })
                            .build()))
            .when(client)
            .getItem(
                GetItemRequest.builder()
                    .tableName("Account")
                    .key(Map.of("accountNumber", AttributeValue.builder().s("111").build()))
                    .build());

        doReturn(CompletableFuture.supplyAsync(() -> PutItemResponse.builder().build()))
            .when(client)
            .putItem(ArgumentMatchers.any(PutItemRequest.class));
      };
}
