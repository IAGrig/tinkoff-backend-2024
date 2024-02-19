package edu.java.bot.bot.links;

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
        for (String domain : allowedDomains) {
            String regex = BEFORE_DOMAIN_URL_PATTERN + domain + AFTER_DOMAIN_URL_PATTERN;
            if (url.toLowerCase().matches(regex)) {
                return domain;
            }
        }
        throw new IllegalArgumentException("Unsupported domain");
    }
}
