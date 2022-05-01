package per.shantanu.poc.sailpoint.entities;

import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Account {

  private String accountNumber;

  private String customerID;

  private Double balance;

  public static @NonNull Account create(@NonNull Consumer<Account.Options> builder) {
    final Account.Options options = new Options();
    builder.accept(options);
    options.validate();
    return options.obj;
  }

  public static final class Options {

    private final Account obj;

    private Options() {
      this.obj = new Account();
    }

    public void accountNumber(String accountNumber) {
      this.obj.accountNumber = accountNumber;
    }

    public void customerID(String customerID) {
      this.obj.customerID = customerID;
    }

    public void balance(Double balance) {
      this.obj.balance = balance;
    }

    private void validate() {

      if (obj.customerID == null || obj.customerID.isEmpty()) {
        throw new IllegalArgumentException("Customer ID cannot be null/empty");
      }

      if (obj.balance == null || obj.balance < 0) {
        throw new IllegalArgumentException("Balance cannot be null or negative");
      }
    }
  }
}
