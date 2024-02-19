package edu.java.bot.links;

import edu.java.bot.bot.links.LinksHandler;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class LinksHandlerTest {

    @Test
    @DisplayName("correct validation test")
    public void correctValidationTest() {
        Environment env = Mockito.mock(Environment.class);
        Mockito.when(env.getProperty("app.allowed-domains", ""))
            .thenReturn("test,testing");
        LinksHandler handler = new LinksHandler(env);

        String actual = handler.validateURLAndGetDomain("https://testing.com/api/v1/get?test=123");

        assertThat(actual).isEqualTo("testing");
    }

    @Test
    @DisplayName("domain uppercase test")
    public void domainUpperCaseTest() {
        Environment env = Mockito.mock(Environment.class);
        Mockito.when(env.getProperty("app.allowed-domains", ""))
            .thenReturn("test,testing");
        LinksHandler handler = new LinksHandler(env);

        String actual = handler.validateURLAndGetDomain("https://TESTING.com/api/v1/get?test=123");

        assertThat(actual).isEqualTo("testing");
    }

    @Test
    @DisplayName("wrong domain test")
    public void wrongDomainTest() {
        Environment env = Mockito.mock(Environment.class);
        Mockito.when(env.getProperty("app.allowed-domains", ""))
            .thenReturn("test,testing");
        LinksHandler handler = new LinksHandler(env);

        assertThatThrownBy(() -> handler.validateURLAndGetDomain("https://TESTER.com/api/v1/get?test=123"))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("no allowed domains test")
    public void noAllowedDomainsTest() {
        Environment env = Mockito.mock(Environment.class);
        Mockito.when(env.getProperty("app.allowed-domains", ""))
            .thenReturn("");
        LinksHandler handler = new LinksHandler(env);

        assertThatThrownBy(() -> handler.validateURLAndGetDomain("https://TESTER.com/api/v1/get?test=123"))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
