package edu.java.configuration;

import edu.java.repositories.jooq.JooqLinkRepository;
import edu.java.repositories.jooq.JooqUserRepository;
import edu.java.services.LinkService;
import edu.java.services.UserService;
import edu.java.services.jooq.JooqLinkService;
import edu.java.services.jooq.JooqUserService;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {
    @Bean
    public DSLContext dslContext(DataSource dataSource) {
        return new DefaultDSLContext(dataSource, SQLDialect.POSTGRES);
    }

    @Bean
    public DefaultConfigurationCustomizer postgresJooqCustomizer() {
        return (DefaultConfiguration c) -> c.settings()
            .withRenderSchema(false)
            .withRenderFormatted(true)
            .withRenderNameCase(RenderNameCase.LOWER)
            .withRenderQuotedNames(RenderQuotedNames.NEVER);
    }

    @Bean
    public UserService jooqUserService(JooqUserRepository jooqUserRepository) {
        return new JooqUserService(jooqUserRepository);
    }

    @Bean
    public LinkService jooqLinkService(JooqLinkRepository jooqLinkRepository) {
        return new JooqLinkService(jooqLinkRepository);
    }
}
