package per.shantanu.poc.atm.handlers;

import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import per.shantanu.poc.atm.config.ApplicationConfiguration;
import per.shantanu.poc.atm.config.DynamoDbConfiguration;
import per.shantanu.poc.atm.entities.Credentials;
import per.shantanu.poc.atm.mocks.MockData;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@SpringBootTest
@ContextConfiguration(
    classes = {AtmServiceControllerTest.TestConfig.class, ATMServiceController.class})
@ActiveProfiles("test")
class AtmServiceControllerTest {

  @Autowired private ATMServiceController controller;

  @Test
  @DisplayName("login is successful")
  void loginSuccessfulTest() {
    final String customerID = "test1";
    final String pin = "1234";

    StepVerifier.create(controller.login(Credentials.create(customerID, pin)))
        .consumeNextWith(t -> Assertions.assertEquals(HttpStatus.OK, t.getStatusCode()))
        .verifyComplete();
  }

  @Test
  @DisplayName("login pin is not 4 digits")
  void loginPinNot4DigitsTest() {
    final String customerID = "test2";
    final String pin = "123";

    Assertions.assertThrows(
        IllegalArgumentException.class, () -> Credentials.create(customerID, pin));
  }

  @Test
  @DisplayName("login customerID is not found in the database")
  void loginCustomerIDNotFoundInDBTest() {
    final String customerID = "wrong1";
    final String pin = "3312";

    StepVerifier.create(controller.login(Credentials.create(customerID, pin)))
        .consumeNextWith(t -> Assertions.assertEquals(HttpStatus.UNAUTHORIZED, t.getStatusCode()))
        .verifyComplete();
  }

  @Test
  @DisplayName("login pin does not match with database record")
  void loginInvalidCredentialsIsUnauthorizedTest() {
    final String customerID = "test1";
    final String pin = "8821";

    StepVerifier.create(controller.login(Credentials.create(customerID, pin)))
        .consumeNextWith(t -> Assertions.assertEquals(HttpStatus.UNAUTHORIZED, t.getStatusCode()))
        .verifyComplete();
  }

  @Test
  @DisplayName("Viewing the details of an account that exists in the database")
  void accountExistsInDBTest() {
    final String accountNumber = "110";

    StepVerifier.create(controller.accountDetails(accountNumber))
        .consumeNextWith(
            t -> {
              Assertions.assertEquals(HttpStatus.OK, t.getStatusCode());
              Assertions.assertEquals(
                  accountNumber, Objects.requireNonNull(t.getBody()).accountNumber());
              Assertions.assertEquals("test1", Objects.requireNonNull(t.getBody().customerID()));
              Assertions.assertEquals(1000D, Objects.requireNonNull(t.getBody().balance()));
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Viewing the details of an account that does not exist in the database")
  void accountDoesNotExistInDBTest() {
    final String accountNumber = "111";

    StepVerifier.create(controller.accountDetails(accountNumber))
        .consumeNextWith(t -> Assertions.assertEquals(HttpStatus.NOT_FOUND, t.getStatusCode()))
        .verifyComplete();
  }

  @Test
  @DisplayName("Deposit a valid amount into an account that exists in the database")
  void depositValidAmountIntoExistingAccountTest() {
    final ATMServiceController.Amount amount = new ATMServiceController.Amount(200D);
    final String accountNumber = "110";

    StepVerifier.create(controller.depositAmount(accountNumber, amount))
        .consumeNextWith(
            t -> {
              Assertions.assertEquals(HttpStatus.OK, t.getStatusCode());
              Assertions.assertEquals(
                  accountNumber, Objects.requireNonNull(t.getBody()).accountNumber());
              Assertions.assertEquals("test1", Objects.requireNonNull(t.getBody()).customerID());
              Assertions.assertEquals(1200D, Objects.requireNonNull(t.getBody()).balance());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Deposit a valid amount into an account that doesn't exist in the database")
  void depositValidAmountIntoNonExistentAccountTest() {
    final ATMServiceController.Amount amount = new ATMServiceController.Amount(200D);
    final String accountNumber = "111";

    StepVerifier.create(controller.depositAmount(accountNumber, amount))
        .consumeNextWith(t -> Assertions.assertEquals(HttpStatus.NOT_FOUND, t.getStatusCode()))
        .verifyComplete();
  }

  @Test
  @DisplayName("Deposit an invalid amount into any account")
  void depositInvalidAmountIntoAccountTest() {
    final ATMServiceController.Amount amount = new ATMServiceController.Amount(-200D);
    final String accountNumber = "110";

    StepVerifier.create(controller.depositAmount(accountNumber, amount))
        .consumeNextWith(t -> Assertions.assertEquals(HttpStatus.BAD_REQUEST, t.getStatusCode()))
        .verifyComplete();
  }

  @Test
  @DisplayName("Withdraw a valid amount from an existing database")
  void withdrawValidAmountFromExistingAccountTest() {
    final ATMServiceController.Amount amount = new ATMServiceController.Amount(200D);
    final String accountNumber = "110";

    StepVerifier.create(controller.withdrawAmount(accountNumber, amount))
        .consumeNextWith(
            t -> {
              Assertions.assertEquals(HttpStatus.OK, t.getStatusCode());
              Assertions.assertEquals(
                  accountNumber, Objects.requireNonNull(t.getBody()).accountNumber());
              Assertions.assertEquals("test1", Objects.requireNonNull(t.getBody()).customerID());
              Assertions.assertEquals(800D, Objects.requireNonNull(t.getBody()).balance());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Withdraw a valid amount from a non-existing database")
  void withdrawValidAmountFromNonExistingAccountTest() {
    final ATMServiceController.Amount amount = new ATMServiceController.Amount(200D);
    final String accountNumber = "111";

    StepVerifier.create(controller.withdrawAmount(accountNumber, amount))
        .consumeNextWith(t -> Assertions.assertEquals(HttpStatus.NOT_FOUND, t.getStatusCode()))
        .verifyComplete();
  }

  @Test
  @DisplayName("Withdraw a negative amount from any account")
  void withdrawNegativeAmountFromAccountTest() {
    final ATMServiceController.Amount amount = new ATMServiceController.Amount(-200D);
    final String accountNumber = "110";

    StepVerifier.create(controller.withdrawAmount(accountNumber, amount))
        .consumeNextWith(t -> Assertions.assertEquals(HttpStatus.BAD_REQUEST, t.getStatusCode()))
        .verifyComplete();
  }

  @Test
  @DisplayName("Withdraw an amount greater than the account balance")
  void withdrawAmountMoreThanBalanceFromAccountTest() {
    final ATMServiceController.Amount amount = new ATMServiceController.Amount(2000D);
    final String accountNumber = "110";

    StepVerifier.create(controller.withdrawAmount(accountNumber, amount))
        .consumeNextWith(t -> Assertions.assertEquals(HttpStatus.BAD_REQUEST, t.getStatusCode()))
        .verifyComplete();
  }

  @TestConfiguration
  @Import({ApplicationConfiguration.class, DynamoDbConfiguration.class})
  public static class TestConfig {

    @Bean
    @Primary
    public DynamoDbAsyncClient client() {
      final DynamoDbAsyncClient client = Mockito.mock(DynamoDbAsyncClient.class);
      MockData.DYNAMODB_MOCKER.accept(client);
      return client;
    }
  }
}
