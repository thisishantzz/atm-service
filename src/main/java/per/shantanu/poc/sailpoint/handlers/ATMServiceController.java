package per.shantanu.poc.sailpoint.handlers;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import per.shantanu.poc.sailpoint.entities.Account;
import per.shantanu.poc.sailpoint.entities.Credentials;
import per.shantanu.poc.sailpoint.errors.ResourceNotFoundError;
import per.shantanu.poc.sailpoint.repositories.AccountRepository;
import per.shantanu.poc.sailpoint.repositories.CredentialsRepository;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class ATMServiceController {

  private final CredentialsRepository credentialsRepository;

  private final AccountRepository accountRepository;

  @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<Void>> login(@RequestBody Credentials credentials) {
    return credentialsRepository
        .findByCustomerID(credentials.customerID())
        .filter(t -> Objects.equals(t.pin(), credentials.pin()))
        .flatMap(t -> Mono.<ResponseEntity<Void>>fromCallable(() -> ResponseEntity.ok().build()))
        .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.status(401).build()))
        .onErrorResume(
            IllegalArgumentException.class::isInstance,
            ex -> Mono.fromCallable(() -> ResponseEntity.status(400).build()))
        .onErrorResume(
            ResourceNotFoundError.class::isInstance,
            ex -> Mono.fromCallable(() -> ResponseEntity.status(401).build()));
  }

  @GetMapping(path = "/account/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<Account>> accountDetails(
      @PathVariable("number") String accountNumber) {
    return accountRepository
        .findByID(accountNumber)
        .flatMap(t -> Mono.fromCallable(() -> ResponseEntity.ok(t)))
        .onErrorResume(
            IllegalArgumentException.class::isInstance,
            ex -> Mono.fromCallable(() -> ResponseEntity.status(400).build()))
        .onErrorResume(
            ResourceNotFoundError.class::isInstance,
            ex -> Mono.fromCallable(() -> ResponseEntity.status(404).build()));
  }

  @PatchMapping(
      path = "/account/{number}/deposit",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<Account>> depositAmount(
      @PathVariable("number") String accountNumber, @RequestBody Amount amount) {
    if (amount.value <= 0) {
      return Mono.just(ResponseEntity.status(400).build());
    }

    return accountRepository
        .findByID(accountNumber)
        .flatMap(
            t ->
                Mono.fromCallable(
                    () -> {
                      final Account account =
                          Account.create(
                              b -> {
                                b.accountNumber(accountNumber);
                                b.customerID(t.customerID());
                                b.balance(t.balance() + amount.value);
                              });
                      return ResponseEntity.ok(account);
                    }))
        .doOnSuccess(
            t -> {
              log.info("Updating account balance due to deposit");
              accountRepository.updateAccount(t.getBody()).subscribe();
            })
        .onErrorResume(
            IllegalArgumentException.class::isInstance,
            ex ->
                Mono.fromCallable(
                    () -> {
                      log.error(ex.getMessage(), ex);
                      return ResponseEntity.status(400).build();
                    }))
        .onErrorResume(
            ResourceNotFoundError.class::isInstance,
            ex ->
                Mono.fromCallable(
                    () -> {
                      log.error(ex.getMessage(), ex);
                      return ResponseEntity.status(404).build();
                    }));
  }

  @PatchMapping(
      path = "/account/{number}/withdraw",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<Account>> withdrawAmount(
      @PathVariable("number") String accountNumber, @RequestBody Amount amount) {
    if (amount.value <= 0) {
      log.error("Amount to withdraw is less than the balance");
      return Mono.fromCallable(() -> ResponseEntity.status(400).build());
    }

    return accountRepository
        .findByID(accountNumber)
        .filter(t -> t.balance() >= amount.value)
        .switchIfEmpty(
            Mono.error(
                new IllegalArgumentException(
                    "Amount to withdraw is greater than available " + "balance")))
        .flatMap(
            t ->
                Mono.fromCallable(
                    () -> {
                      final Account account =
                          Account.create(
                              b -> {
                                b.accountNumber(accountNumber);
                                b.customerID(t.customerID());
                                b.balance(t.balance() - amount.value);
                              });
                      return ResponseEntity.ok(account);
                    }))
        .doOnSuccess(
            t -> {
              log.info("Updating account balance due to withdrawal");
              accountRepository.updateAccount(t.getBody()).subscribe();
            })
        .onErrorResume(
            IllegalArgumentException.class::isInstance,
            ex ->
                Mono.fromCallable(
                    () -> {
                      log.error(ex.getMessage(), ex);
                      return ResponseEntity.status(400).build();
                    }))
        .onErrorResume(
            ResourceNotFoundError.class::isInstance,
            ex ->
                Mono.fromCallable(
                    () -> {
                      log.error(ex.getMessage(), ex);
                      return ResponseEntity.status(404).build();
                    }));
  }

  @RequiredArgsConstructor
  public static final class Amount {

    private final Double value;
  }

  @ControllerAdvice
  public static final class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public <T> Mono<ResponseEntity<T>> illegalArgument(IllegalArgumentException ex) {
      log.error(ex.getMessage(), ex);
      return Mono.fromCallable(() -> ResponseEntity.status(400).build());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ResourceNotFoundError.class)
    public <T> Mono<ResponseEntity<T>> resourceNotFound(ResourceNotFoundError ex) {
      log.error(ex.getMessage(), ex);
      return Mono.fromCallable(() -> ResponseEntity.status(404).build());
    }
  }
}
