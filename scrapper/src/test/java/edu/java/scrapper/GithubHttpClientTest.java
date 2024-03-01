package edu.java.scrapper;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.github.GithubHttpClient;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@WireMockTest
@NoArgsConstructor
public class GithubHttpClientTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);
    private GithubHttpClient client;

    @Before
    public void setUp() {
        String baseUrl = wireMockRule.baseUrl();
        client = new GithubHttpClient(baseUrl);
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
