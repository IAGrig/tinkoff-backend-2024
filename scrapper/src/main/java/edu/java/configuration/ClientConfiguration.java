package edu.java.configuration;

import edu.java.httpClients.BotHttpClient;
import edu.java.httpClients.HttpClient;
import edu.java.httpClients.github.GithubHttpClient;
import edu.java.httpClients.retry.BackOffPolicy;
import edu.java.httpClients.stackoverflow.StackoverflowHttpClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration("scrapperClientConfiguration")
public class ClientConfiguration {
    @Value("${scrapper.stackoverflow.baseUrl:https://api.stackexchange.com/2.3}")
    private String baseStackoverflowUrlDefault;
    @Value("${scrapper.stackoverflow.questionsPath:/questions}")
    private String stackoverflowQuestionsPath;
    @Value("${scrapper.stackoverflow.questionAnswersPath:/answers}")
    private String stackoverflowQuestionAnswersPath;
    @Value("${scrapper.stackoverflow.questionCommentsPath:/comments}")
    private String stackoverflowQuestionCommentsPath;
    @Value("${scrapper.stackoverflow.backOffPolicy:constant}")
    private String stackoverflowBackOffPolicyStr;
    @Value("${scrapper.stackoverflow.retryCodes:}")
    private List<Integer> stackoverflowRetryCodes;

    @Value("${scrapper.github.baseUrl:https://api.github.com}")
    private String baseGithubUrlDefault;
    @Value("${scrapper.github.backOffPolicy:constant}")
    private String githubBackOffPolicyStr;
    @Value("${scrapper.github.retryCodes:}")
    private List<Integer> githubRetryCodes;

    @Value("${clients.bot.baseUrl:http://localhost:8090}")
    private String baseBotUrlDefault;
    @Value("${clients.bot.updatesPath:/updates}")
    private String botUpdatesPath;
    @Value("${clients.bot.backOffPolicy:constant}")
    private String botBackOffPolicyStr;
    @Value("${clients.bot.retryCodes:}")
    private List<Integer> botRetryCodes;

    @Bean
    @Qualifier("githubHttpClient")
    public HttpClient githubHttpClient(WebClient githubWebClient, BackOffPolicy githubBackOffPolicy) {
        return new GithubHttpClient(githubWebClient, baseGithubUrlDefault, githubBackOffPolicy, githubRetryCodes);
    }

    @Bean
    @Qualifier("stackoverflowHttpClient")
    public HttpClient stackoverflowHttpClient(
        WebClient stackoverflowWebClient,
        BackOffPolicy stackoverflowBackOffPolicy
    ) {
        return new StackoverflowHttpClient(
            stackoverflowWebClient,
            baseStackoverflowUrlDefault,
            stackoverflowQuestionsPath,
            stackoverflowQuestionAnswersPath,
            stackoverflowQuestionCommentsPath,
            stackoverflowBackOffPolicy,
            stackoverflowRetryCodes
        );
    }

    @ConditionalOnProperty(
        value = "app.useQueue",
        havingValue = "false"
    )
    @Bean
    public BotHttpClient botHttpClient(WebClient botWebClient, BackOffPolicy botBackOffPolicy) {
        return new BotHttpClient(botWebClient, botUpdatesPath, botBackOffPolicy, botRetryCodes);
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

    @Bean
    public BackOffPolicy githubBackOffPolicy() {
        return BackOffPolicy.valueOf(githubBackOffPolicyStr.toUpperCase());
    }

    @Bean
    public BackOffPolicy stackoverflowBackOffPolicy() {
        return BackOffPolicy.valueOf(stackoverflowBackOffPolicyStr.toUpperCase());
    }

    @Bean
    public BackOffPolicy botBackOffPolicy() {
        return BackOffPolicy.valueOf(botBackOffPolicyStr.toUpperCase());
    }
}
