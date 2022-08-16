import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoTest {
    @Test
    public void monoSubscriber() {
        String name = "Taylor Swift";
        Mono<String> mono = Mono.just(name).log();
        mono.subscribe();
        log.info("------------");
        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumer() {
        String name = "Taylor Swift";
        Mono<String> mono = Mono.just(name).log();
        mono.subscribe(s -> log.info("Subscriber: {}", s));
        log.info("------------");
        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerError() {
        String name = "Taylor Swift";
        Mono<String> mono = Mono.just(name)
                .map(s -> {
                    throw new RuntimeException("Something went wrong");
                });
        mono.subscribe(s -> log.info("Subscriber: {}", s), Throwable::printStackTrace);
        log.info("------------");
        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void monoSubscriberConsumerComplete() {
        String name = "Taylor Swift";
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);
        mono.subscribe(s -> log.info("Subscriber: {}", s), Throwable::printStackTrace, () -> log.info("Completed"));
    }

    @Test
    public void monoSubscriberConsumerSubscription() {
        String name = "Taylor Swift";
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);
        mono.subscribe(s -> log.info("Subscriber: {}", s), Throwable::printStackTrace, () -> log.info("Completed"),
                subscription -> subscription.request(11));
    }

    @Test
    public void monoDoOnMethods() {
        String name = "Taylor Swift";
        Mono<String> mono = Mono.just(name)
//                .log()
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(num -> log.info("Requested: {}", num))
                .doOnNext(s -> log.info("Next: {}", s))
                .doOnSuccess(s -> log.info("Success: {}", s))
                .map(String::toUpperCase);
        mono.subscribe(s -> log.info("Subscriber: {}", s), Throwable::printStackTrace, () -> log.info("Completed"),
                subscription -> subscription.request(11));
    }
}
