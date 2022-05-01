package per.shantanu.poc.sailpoint.init;

import reactor.core.publisher.Mono;

public interface Initializer {

  Mono<Void> initialize();
}
