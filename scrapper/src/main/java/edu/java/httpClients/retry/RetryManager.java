package edu.java.httpClients.retry;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RetryManager {
    public static RetryBackoffSpec getBackoffSpec(BackOffPolicy backOffPolicy, int delay, int attempts) {
        return switch (backOffPolicy) {
            case CONSTANT -> Retry.fixedDelay(attempts, Duration.ofSeconds(delay));
            case LINEAR -> null; // TODO
            case EXPONENT -> Retry.backoff(attempts, Duration.ofSeconds(delay));
        };
    }
}
