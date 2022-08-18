import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
public class FluxTest {
    @Test
    public void fluxSubscriber() {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5).log();
        StepVerifier.create(flux).expectNext(1, 2, 3, 4, 5).verifyComplete();
    }

    @Test
    public void fluxSubscriberRange() {
        Flux<Integer> flux = Flux.range(1, 5).log();
        StepVerifier.create(flux).expectNext(1, 2, 3, 4, 5).verifyComplete();
    }

    @Test
    public void fluxSubscriberFromList() {
        Flux<Integer> flux = Flux.fromIterable(List.of(1, 2, 3, 4, 5)).log();
        StepVerifier.create(flux).expectNext(1, 2, 3, 4, 5).verifyComplete();
    }

    @Test
    public void fluxSubscriberError() {
        Flux<Integer> flux = Flux.range(1, 5).log().map(i -> {
            if (i == 3) {
                throw new RuntimeException("Error");
            }
            return i;
        });
        flux.subscribe(i -> log.info("{}", i), Throwable::printStackTrace, () -> log.info("Completed"),
                subscription -> subscription.request(2));
        StepVerifier.create(flux).expectNext(1, 2).expectError(RuntimeException.class).verify();
    }

    @Test
    public void fluxSubscriberUglyBackpressure() {
        Flux<Integer> flux = Flux.range(1, 10).log();
        flux.subscribe(new Subscriber<Integer>() {
            private int count = 0;
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;
                subscription.request(2);
            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
        StepVerifier.create(flux).expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).verifyComplete();
    }
}
