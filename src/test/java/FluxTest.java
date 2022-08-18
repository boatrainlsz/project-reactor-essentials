import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
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
        flux.subscribe(i -> log.info("{}", i), Throwable::printStackTrace, () -> log.info("Completed"));
        StepVerifier.create(flux).expectNext(1, 2)
                .expectError(RuntimeException.class)
                .verify();
    }
}
