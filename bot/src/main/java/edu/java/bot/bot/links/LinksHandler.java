package edu.java.bot.bot.links;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinksHandler {
    private final static String BEFORE_DOMAIN_URL_PATTERN = "https?://(?:[a-z]+\\.)*";
    private final static String AFTER_DOMAIN_URL_PATTERN = "\\.[a-z]+(?:/\\S+)*(?:\\?\\S+)*";
    private final Environment env;

    public String validateURLAndGetDomain(String url) {
        String[] allowedDomains = env.getProperty("app.allowed-domains", "").split(",");
        URI uri = URI.create(url);
        String host = uri.getHost();

        for (String domain : allowedDomains) {
            if (domain.equalsIgnoreCase(host)) {
                return domain;
            }
        }
        throw new IllegalArgumentException("Unsupported domain");
    }
}
