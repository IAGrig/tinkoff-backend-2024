package edu.java.httpClients;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.httpClients.github.GithubHttpClient;
import edu.java.httpClients.retry.BackOffPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@WireMockTest
public class GithubHttpClientTest {
    private GithubHttpClient client;
    private WebClient webClient;
    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8092);
        wireMockServer.start();

        WireMock.configureFor("localhost", 8092);

        webClient = WebClient.builder()
            .baseUrl("http://localhost:8092")
            .build();

        client = new GithubHttpClient(webClient, "http://localhost:8092", BackOffPolicy.LINEAR, List.of(404));
    }

    @AfterEach
    public void cleanUp() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Successful getLastUpdate")
    public void getLastUpdateSuccessfulTest() {
        String responseBody =
            "{\"id\": 123, \"updated_at\": \"2023-10-15T16:30:43Z\", \"pushed_at\": \"2024-01-24T14:16:31Z\"}";
        stubFor(WireMock.get("/repositories/123")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(responseBody)
            ));

        var res = client.getLastUpdate(123L);

        assertThat(res.getId()).isEqualTo(123L);
        assertThat(res.getUpdatedAt()).isEqualTo("2023-10-15T16:30:43Z");
        assertThat(res.getPushedAt()).isEqualTo("2024-01-24T14:16:31Z");
    }

    @Test
    @DisplayName("404 for getLastUpdate")
    public void getLastUpdate404Test() {
        stubFor(WireMock.get("/repositories/123")
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")));

        var res = client.getLastUpdate(123L);

        assertThat(res.getId()).isNull();
        assertThat(res.getUpdatedAt()).isNull();
        assertThat(res.getPushedAt()).isNull();
    }

    @Test
    @DisplayName("Successful owner/repository getLastUpdate")
    public void getLastUpdateSuccessfulMultipleIdsTest() {
        String responseBody =
            "{\"id\": 123, \"updated_at\": \"2023-10-15T16:30:43Z\", \"pushed_at\": \"2024-01-24T14:16:31Z\"}";
        stubFor(WireMock.get("/repos/tester/repo")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(responseBody)
            ));

        var res = client.getLastUpdate("tester", "repo");

        assertThat(res.getId()).isEqualTo(123L);
        assertThat(res.getUpdatedAt()).isEqualTo("2023-10-15T16:30:43Z");
        assertThat(res.getPushedAt()).isEqualTo("2024-01-24T14:16:31Z");
    }
}
