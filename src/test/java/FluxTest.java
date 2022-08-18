import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
public class FluxTest {
    @Test
    public void fluxSubscriber() {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5).log();
        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberRange() {
        Flux<Integer> flux = Flux.range(1, 5).log();
        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }
}
