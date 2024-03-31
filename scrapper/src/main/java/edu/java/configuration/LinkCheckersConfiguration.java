package edu.java.configuration;

import edu.java.httpClients.HttpClient;
import edu.java.httpClients.github.GithubHttpClient;
import edu.java.httpClients.stackoverflow.StackoverflowHttpClient;
import edu.java.httpClients.utils.GithubLinkChecker;
import edu.java.httpClients.utils.StackoverflowLinkChecker;
import edu.java.services.LinkService;
import edu.java.services.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LinkCheckersConfiguration {
    @Bean
    public GithubLinkChecker githubLinkChecker(
        HttpClient githubHttpClient,
        @Qualifier("jdbcLinkService") LinkService linkService,
        @Qualifier("jdbcUserService") UserService userService
    ) {
        return new GithubLinkChecker(
            (GithubHttpClient) githubHttpClient, linkService, userService);
    }

    @Bean
    public StackoverflowLinkChecker stackoverflowLinkChecker(
        HttpClient stackoverflowHttpClient,
        @Qualifier("jdbcLinkService") LinkService linkService,
        @Qualifier("jdbcUserService") UserService userService
    ) {
        return new StackoverflowLinkChecker(
            (StackoverflowHttpClient) stackoverflowHttpClient, linkService, userService);
    }
}
