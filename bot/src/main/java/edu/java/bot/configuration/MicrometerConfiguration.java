package edu.java.bot.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicrometerConfiguration {
    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public Counter processedUpdatesCounter(ApplicationConfig config, MeterRegistry registry) {
        return Counter.builder(config.metrics().processedUpdatesCount().name())
            .description(config.metrics().processedUpdatesCount().description())
            .tag("application", applicationName)
            .register(registry);
    }
}
