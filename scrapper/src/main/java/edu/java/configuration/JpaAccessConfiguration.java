package edu.java.configuration;

import edu.java.repositories.jpa.JpaLinkRepository;
import edu.java.repositories.jpa.JpaUserRepository;
import edu.java.services.LinkService;
import edu.java.services.UserService;
import edu.java.services.jpa.JpaLinkService;
import edu.java.services.jpa.JpaUserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {

    @Bean
    public UserService jpaUserService(JpaUserRepository jpaUserRepository) {
        return new JpaUserService(jpaUserRepository);
    }

    @Bean
    public LinkService jpaLinkService(JpaLinkRepository jpaLinkRepository) {
        return new JpaLinkService(jpaLinkRepository);
    }
}
