package edu.java.httpClients.retry;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.ContextView;
import reactor.util.retry.Retry;

public class LinearRetry extends Retry {
    static final Consumer<RetrySignal> NO_OP_CONSUMER = (rs) -> {
    };
    static final BiFunction<RetrySignal, Mono<Void>, Mono<Void>> NO_OP_BIFUNCTION = (rs, m) -> {
        return m;
    };
    private final int delay;
    private final int maxAttempts;
    private final Predicate<Throwable> errorFilter;
    private int attempt;

    public LinearRetry(int delay, int maxAttempts, Predicate<Throwable> errorFilter) {
        this.delay = delay;
        this.maxAttempts = maxAttempts;
        this.errorFilter = errorFilter;
    }

    @Override
    public Publisher<?> generateCompanion(Flux<RetrySignal> flux) {
        return Flux.deferContextual((cv) -> {
            return flux.contextWrite(cv).concatMap((retryWhenState) -> {
                RetrySignal copy = retryWhenState.copy();
                Throwable currentFailure = copy.failure();
                if (currentFailure == null) {
                    return Mono.error(new IllegalStateException("Retry.RetrySignal#failure() not expected to be null"));
                } else if (!this.errorFilter.test(currentFailure) || attempt >= this.maxAttempts) {
                    return Mono.error(currentFailure);
                } else {
                    Duration nextBackoff = Duration.ofSeconds(delay + attempt++);

                    if (nextBackoff.isZero()) {
                        return applyHooks(copy, Mono.just(attempt), (rs) -> {
                        }, (rs) -> {
                        }, (rs, m) -> {
                            return null;
                        }, (rs, m) -> {
                            return null;
                        }, cv);
                    } else {
                        return applyHooks(copy, Mono.delay(nextBackoff, Schedulers.parallel()), (rs) -> {
                        }, (rs) -> {
                        }, (rs, m) -> {
                            return null;
                        }, (rs, m) -> {
                            return null;
                        }, cv);
                    }
                }
            }).onErrorStop();
        });
    }

    public <T> Mono<T> applyHooks(
        RetrySignal copyOfSignal,
        Mono<T> originalCompanion,
        Consumer<RetrySignal> doPreRetry,
        Consumer<RetrySignal> doPostRetry,
        BiFunction<RetrySignal, Mono<Void>, Mono<Void>> asyncPreRetry,
        BiFunction<RetrySignal, Mono<Void>, Mono<Void>> asyncPostRetry,
        ContextView cv
    ) {
        if (doPreRetry != NO_OP_CONSUMER) {
            try {
                doPreRetry.accept(copyOfSignal);
            } catch (Throwable throwable) {
                return Mono.error(throwable);
            }
        }

        Mono postRetrySyncMono;
        if (doPostRetry != NO_OP_CONSUMER) {
            postRetrySyncMono = Mono.fromRunnable(() -> {
                doPostRetry.accept(copyOfSignal);
            });
        } else {
            postRetrySyncMono = Mono.empty();
        }

        Mono<Void> preRetryMono =
            asyncPreRetry == NO_OP_BIFUNCTION ? Mono.empty() : asyncPreRetry.apply(copyOfSignal, Mono.empty());
        Mono<Void> postRetryMono =
            asyncPostRetry != NO_OP_BIFUNCTION ? (Mono) asyncPostRetry.apply(copyOfSignal, postRetrySyncMono)
                : postRetrySyncMono;
        Mono var10000 = preRetryMono.then(originalCompanion);
        postRetryMono.getClass();
        return var10000.flatMap(postRetryMono::thenReturn).contextWrite(cv);
    }
}
