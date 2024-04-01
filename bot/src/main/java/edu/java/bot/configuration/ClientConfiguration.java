package edu.java.bot.configuration;

import edu.java.bot.httpClients.BackOffPolicy;
import edu.java.bot.httpClients.ScrapperHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration("botClientConfiguration")
public class ClientConfiguration {
    @Value("${clients.scrapper.baseUrl:http://localhost:8080}")
    private String baseScrapperUrlDefault;

    @Value("${clients.scrapper.tgChatPath:/tg-chat}")
    private String scrapperTgChatPath;

    @Value("${clients.scrapper.linksPath:/links}")
    private String scrapperLinksPath;

    @Value("${clients.scrapper.chatIdHeader:Tg-Chat-Id}")
    private String scrapperTgChatIdHeader;
    @Value("${clients.scrapper.backOffPolicy:constant}")
    private String scrapperBackOffPolicyStr;

    @Bean
    public ScrapperHttpClient scrapperHttpClient(WebClient scrapperWebClient, BackOffPolicy scrapperBackOffPolicy) {
        return new ScrapperHttpClient(
            scrapperWebClient,
            scrapperTgChatPath,
            scrapperLinksPath,
            scrapperTgChatIdHeader,
            scrapperBackOffPolicy
        );
    }

    @Bean
    public WebClient scrapperWebClient() {
        return WebClient.builder()
            .baseUrl(baseScrapperUrlDefault)
            .build();
    }

    @Bean
    public BackOffPolicy scrapperBackOffPolicy() {
        return BackOffPolicy.valueOf(scrapperBackOffPolicyStr.toUpperCase());
    }
}
