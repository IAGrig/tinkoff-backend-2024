package edu.java.configuration;

import edu.database.Database;
import edu.database.StubDatabase;
import java.util.HashMap;
import java.util.HashSet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {
    @Bean // maybe I should delete this useless thing
    @ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "stub")
    public Database stubDatabase() {
        return new StubDatabase(new HashMap<>(), new HashMap<>(), new HashSet<>());
    }
}
