package per.shantanu.poc.sailpoint.repositories;

import org.springframework.util.StringUtils;
import per.shantanu.poc.sailpoint.entities.Credentials;
import per.shantanu.poc.sailpoint.errors.ResourceNotFoundError;
import reactor.core.publisher.Mono;

public abstract class CredentialsRepository {

  public Mono<Credentials> findByCustomerID(String customerID) {
    if (!StringUtils.hasText(customerID)) {
      return Mono.error(new IllegalArgumentException("customerID cannot be null/empty"));
    }

    return findCredentialsInDB(customerID)
        .switchIfEmpty(Mono.error(new ResourceNotFoundError("Credentials", customerID)));
  }

  public Mono<Credentials> createCredential(Credentials credentials) {
    if (credentials == null) {
      return Mono.error(new IllegalArgumentException("credentials cannot be null"));
    }
    return createCredentialInDB(credentials);
  }

  protected abstract Mono<Credentials> findCredentialsInDB(String customerID);

  protected abstract Mono<Credentials> createCredentialInDB(Credentials credentials);
}
