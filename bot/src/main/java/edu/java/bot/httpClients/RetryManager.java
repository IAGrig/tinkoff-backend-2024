package edu.java.bot.httpClients;

import java.time.Duration;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RetryManager {
    private static final double JITTER_FACTOR = 0.2;

    private static boolean filter(Throwable throwable, List<Integer> retryCodes) {
        return throwable instanceof WebClientResponseException
            && retryCodes.contains(((WebClientResponseException) throwable).getStatusCode().value());
    }

    public static Retry getBackoffSpec(BackOffPolicy backOffPolicy, List<Integer> retryCodes, int delay, int attempts) {
        return switch (backOffPolicy) {
            case CONSTANT -> Retry.fixedDelay(attempts, Duration.ofSeconds(delay)).jitter(JITTER_FACTOR)
                .filter(throwable -> filter(throwable, retryCodes));
            case LINEAR -> new LinearRetry(delay, attempts, throwable -> filter(throwable, retryCodes));
            case EXPONENT -> Retry.backoff(attempts, Duration.ofSeconds(delay)).jitter(JITTER_FACTOR)
                .filter(throwable -> filter(throwable, retryCodes));
        };
    }
}
