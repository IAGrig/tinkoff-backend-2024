package edu.java.configuration;

import edu.java.scrapper.github.GithubHttpClient;
import edu.java.scrapper.stackoverflow.StackoverflowHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Value("${scrapper.stackoverflow.baseUrl}")
    private String baseStackoverflowUrlDefault;

    @Value("${scrapper.github.baseUrl}")
    private String baseGithubUrlDefault;

    @Bean
    public GithubHttpClient githubHttpClient() {
        return new GithubHttpClient(baseGithubUrlDefault);
    }

    @Bean
    public StackoverflowHttpClient stackoverflowHttpClient() {
        return new StackoverflowHttpClient(baseStackoverflowUrlDefault);
    }
}
