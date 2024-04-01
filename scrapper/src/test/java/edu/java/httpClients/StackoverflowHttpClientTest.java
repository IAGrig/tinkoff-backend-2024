package edu.java.httpClients;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.httpClients.retry.BackOffPolicy;
import edu.java.httpClients.stackoverflow.StackoverflowHttpClient;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@WireMockTest
public class StackoverflowHttpClientTest {
    private WebClient webClient;
    private StackoverflowHttpClient client;
    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8092);
        wireMockServer.start();

        WireMock.configureFor("localhost", 8092);

        webClient = WebClient.builder()
            .baseUrl("http://localhost:8092")
            .build();

        client = new StackoverflowHttpClient(
            webClient,
            "http://localhost:8092",
            "/questions",
            "/answers",
            "/comments",
            BackOffPolicy.CONSTANT
        );
    }

    @AfterEach
    public void cleanUp() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Successful getLastUpdate")
    public void getLastUpdateSuccessfulTest() {
        String responseBody = "{\"items\": [{\"question_id\": 123, \"last_activity_date\": 1590400952}]}";
        stubFor(WireMock.get(urlPathEqualTo("/questions/123"))
            .withQueryParam("order", equalTo("desc"))
            .withQueryParam("sort", equalTo("activity"))
            .withQueryParam("site", equalTo("stackoverflow"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(responseBody)
            ));

        var res = client.getLastUpdate(123L);

        assertThat(res.getItems().get(0).getQuestionId()).isEqualTo(123L);
        assertThat(res.getItems().get(0).getLastActivity()).isEqualTo("2020-05-25T10:02:32Z");
    }

    @Test
    @DisplayName("404 for getLastUpdate")
    public void getLastUpdate404Test() {
        stubFor(WireMock.get(urlPathEqualTo("/questions/123"))
            .withQueryParam("order", equalTo("desc"))
            .withQueryParam("sort", equalTo("activity"))
            .withQueryParam("site", equalTo("stackoverflow"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
            ));

        var res = client.getLastUpdate(123L);

        assertThat(res.getItems()).isNull();
    }

    @Test
    @DisplayName("Successful multiple IDs getLastUpdate")
    public void getLastUpdateSuccessfulMultipleIdsTest() {
        String responseBody = "{\"items\": [{\"question_id\": 123, \"last_activity_date\": 1590400952}, " +
            "{\"question_id\": 7555000, \"last_activity_date\": 7555000}]}";
        stubFor(WireMock.get(urlPathEqualTo("/questions/123;135;0;-100;7555000"))
            .withQueryParam("order", equalTo("desc"))
            .withQueryParam("sort", equalTo("activity"))
            .withQueryParam("site", equalTo("stackoverflow"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(responseBody)
            ));

        var res = client.getLastUpdate(List.of(123L, 135L, 0L, -100L, 7555000L));

        assertThat(res.getItems().size()).isEqualTo(2);
        assertThat(res.getItems().get(0).getQuestionId()).isEqualTo(123L);
        assertThat(res.getItems().get(0).getLastActivity()).isEqualTo("2020-05-25T10:02:32Z");
        assertThat(res.getItems().get(1).getQuestionId()).isEqualTo(7555000L);
        assertThat(res.getItems().get(1).getLastActivity()).isEqualTo("1970-03-29T10:36:40Z");
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public WebClient webClient() {
            return WebClient.builder()
                .baseUrl("http://localhost:8092")
                .build();
        }
    }
}
