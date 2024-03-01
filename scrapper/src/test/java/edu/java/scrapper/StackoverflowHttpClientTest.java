package edu.java.scrapper;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.stackoverflow.StackoverflowHttpClient;
import java.util.List;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@WireMockTest
@NoArgsConstructor
public class StackoverflowHttpClientTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);
    private StackoverflowHttpClient client;

    @Before
    public void setUp() {
        String baseUrl = wireMockRule.baseUrl();
        client = new StackoverflowHttpClient(baseUrl);
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
}
