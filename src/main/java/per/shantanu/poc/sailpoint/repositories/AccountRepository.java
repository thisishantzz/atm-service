package per.shantanu.poc.sailpoint.repositories;

import org.springframework.util.StringUtils;
import per.shantanu.poc.sailpoint.entities.Account;
import per.shantanu.poc.sailpoint.errors.ResourceNotFoundError;
import reactor.core.publisher.Mono;

public abstract class AccountRepository {

  public Mono<Account> findByID(String accountNumber) {
    if (!StringUtils.hasText(accountNumber)) {
      return Mono.error(new IllegalArgumentException("account number cannot be null/empty"));
    }

    return findAccountInDB(accountNumber)
        .switchIfEmpty(Mono.error(new ResourceNotFoundError("Account", accountNumber)));
  }

  public Mono<Account> createAccount(Account account) {
    if (account == null) {
      return Mono.error(new IllegalArgumentException("account details cannot be null/emtpy"));
    }

    return createAccountInDB(account);
  }

  public Mono<Void> updateAccount(Account account) {
    if (account == null) {
      return Mono.error(new IllegalArgumentException("account details cannot be null/empty"));
    }

    if (!StringUtils.hasText(account.accountNumber())) {
      return Mono.error(
          new IllegalArgumentException("account number to update cannot be null/empty"));
    }

    return updateAccountInDB(account);
  }

  protected abstract Mono<Account> findAccountInDB(String accountNumber);

  protected abstract Mono<Account> createAccountInDB(Account account);

  protected abstract Mono<Void> updateAccountInDB(Account account);
}
