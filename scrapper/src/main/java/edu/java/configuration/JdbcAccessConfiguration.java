package edu.java.configuration;

import edu.java.repositories.jdbc.JdbcLinkRepository;
import edu.java.repositories.jdbc.JdbcUserRepository;
import edu.java.services.LinkService;
import edu.java.services.UserService;
import edu.java.services.jdbc.JdbcLinkService;
import edu.java.services.jdbc.JdbcUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
    public UserService jdbcUserService(JdbcUserRepository jdbcUserRepository) {
        return new JdbcUserService(jdbcUserRepository);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
    public LinkService jdbcLinkService(JdbcLinkRepository jdbcLinkRepository) {
        return new JdbcLinkService(jdbcLinkRepository);
    }
}
