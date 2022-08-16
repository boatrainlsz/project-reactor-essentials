import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
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
        StepVerifier.create(mono).expectNext(name).verifyComplete();
    }

    @Test
    public void monoSubscriberConsumer() {
        String name = "Taylor Swift";
        Mono<String> mono = Mono.just(name).log();
        mono.subscribe(s -> log.info("Subscriber: {}", s));
        log.info("------------");
        StepVerifier.create(mono).expectNext(name).verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerError() {
        String name = "Taylor Swift";
        Mono<String> mono = Mono.just(name).map(s -> {
            throw new RuntimeException("Something went wrong");
        });
        mono.subscribe(s -> log.info("Subscriber: {}", s), Throwable::printStackTrace);
        log.info("------------");
        StepVerifier.create(mono).expectError(RuntimeException.class).verify();
    }

    @Test
    public void monoSubscriberConsumerComplete() {
        String name = "Taylor Swift";
        Mono<String> mono = Mono.just(name).log().map(String::toUpperCase);
        mono.subscribe(s -> log.info("Subscriber: {}", s), Throwable::printStackTrace, () -> log.info("Completed"));
    }

    @Test
    public void monoSubscriberConsumerSubscription() {
        String name = "Taylor Swift";
        Mono<String> mono = Mono.just(name).log().map(String::toUpperCase);
        mono.subscribe(s -> log.info("Subscriber: {}", s), Throwable::printStackTrace, () -> log.info("Completed"),
                subscription -> subscription.request(11));
    }

    @Test
    public void monoDoOnMethods() {
        String name = "Taylor Swift";
        Mono<Object> mono = Mono.just(name)
//                .log()
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(num -> log.info("Requested: {}", num)).doOnNext(s -> log.info("Next: {}", s))
                .flatMap(s -> Mono.empty())
                //这一行不会被执行，因为上一步传来了Mono.empty()
                .doOnNext(s -> log.info("Next: {}", s)).doOnSuccess(s -> log.info("Success: {}", s));
        mono.subscribe(s -> log.info("Subscriber: {}", s), Throwable::printStackTrace, () -> log.info("Completed"),
                subscription -> subscription.request(11));
    }

    @Test
    public void monoOnError() {
        Mono<Object> error =
                Mono.error(new IllegalAccessException("sds")).doOnError(e -> log.info("Error: {}", e)).log();
        StepVerifier.create(error).expectError(IllegalAccessException.class).verify();
    }

    @Test
    public void monoOnErrorResume() {
        String hello = "Hello";
        Mono<Object> error =
                Mono.error(new IllegalAccessException("sds"))
                        .doOnError(e -> log.info("Error: {}", e))
                        .onErrorResume(s -> {
                            log.info("Inside Error Resume: {}", s);
                            return Mono.just(hello);
                        })
                        .log();
        StepVerifier.create(error).expectNext(hello).verifyComplete();
    }
}
