package edu.java.configuration;

import edu.java.httpClients.BotHttpClient;
import edu.java.httpClients.HttpClient;
import edu.java.httpClients.github.GithubHttpClient;
import edu.java.httpClients.stackoverflow.StackoverflowHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfiguration {
    @Value("${scrapper.stackoverflow.baseUrl:https://api.stackexchange.com/2.3}")
    private String baseStackoverflowUrlDefault;

    @Value("${scrapper.stackoverflow.questionsPath:/questions}")
    private String stackoverflowQuestionsPath;

    @Value("${scrapper.github.baseUrl:https://api.github.com}")
    private String baseGithubUrlDefault;

    @Value("${clients.bot.baseUrl:http://localhost:8090}")
    private String baseBotUrlDefault;

    @Value("${clients.bot.updatesPath:/updates}")
    private String botUpdatesPath;

    @Bean
    @Qualifier("githubHttpClient")
    public HttpClient githubHttpClient(WebClient githubWebClient) {
        return new GithubHttpClient(githubWebClient, baseGithubUrlDefault);
    }

    @Bean
    @Qualifier("stackoverflowHttpClient")
    public HttpClient stackoverflowHttpClient(WebClient stackoverflowWebClient) {
        return new StackoverflowHttpClient(
            stackoverflowWebClient,
            baseStackoverflowUrlDefault,
            stackoverflowQuestionsPath
        );
    }

    @Bean
    public BotHttpClient botHttpClient(WebClient botWebClient) {
        return new BotHttpClient(botWebClient, botUpdatesPath);
    }

    @Bean
    public WebClient githubWebClient() {
        return WebClient.builder()
            .baseUrl(baseGithubUrlDefault)
            .defaultHeader("Accept", "application/vnd.github+json")
            .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
            .build();
    }

    @Bean
    public WebClient stackoverflowWebClient() {
        return WebClient.builder()
            .baseUrl(baseStackoverflowUrlDefault)
            .build();
    }

    @Bean
    public WebClient botWebClient() {
        return WebClient.builder()
            .baseUrl(baseBotUrlDefault)
            .build();
    }
}
