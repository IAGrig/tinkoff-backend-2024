package edu.java.services.jooq;

import edu.database.entities.Link;
import edu.database.exceptions.LinkNotFoundException;
import edu.java.exceptions.ApiException;
import edu.java.repositories.IntegrationTest;
import edu.java.repositories.jooq.JooqLinkRepository;
import edu.java.repositories.jooq.JooqUserRepository;
import edu.java.services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = {
    "app.database-access-type=jooq",
    "spring.cache.type=none",
    "bucket4j.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
}
)
public class JooqLinkServiceTest extends IntegrationTest {
    @Autowired
    private LinkService service;
    @Autowired
    private JooqLinkRepository linkRepository;
    @Autowired
    private JooqUserRepository userRepository;

    @Test
    @DisplayName("Add link tracking test")
    public void addLinkTrackingTest() {
        linkRepository.deleteAll();
        userRepository.addUser(722L);
        Link link = linkRepository.createLink("http://jooq.link-service.com", "http://jooq.link-service.com/test");

        service.addLinkTracking(722L, URI.create("http://jooq.link-service.com/test"));

        assertThat(linkRepository.findUserLinks(722L)
            .stream().anyMatch(l -> Objects.equals(l.getUrl(), "http://jooq.link-service.com/test")))
            .isEqualTo(true);
    }

    @Test
    @DisplayName("Add already tracked link test")
    public void addTrackedLinkTrackingTest() {
        linkRepository.deleteAll();
        userRepository.addUser(72L);

        service.addLinkTracking(72L, URI.create("https://jooq.test.com/test-double-tracked"));

        assertThatThrownBy(() -> service.addLinkTracking(72L, URI.create("https://jooq.test.com/test-double-tracked")))
            .isExactlyInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("Add non-existing link tracking test")
    public void addNonExistingLinkTrackingTest() {
        linkRepository.deleteAll();
        userRepository.addUser(721L);

        service.addLinkTracking(721L, URI.create("https://jooq.test.com/non-exist"));

        assertThat(linkRepository.findUserLinks(721L)
            .stream().anyMatch(l -> l.getUrl().equals("https://jooq.test.com/non-exist")))
            .isEqualTo(true);
    }

    @Test
    @DisplayName("Remove tracking test")
    public void removeTrackingTest() {
        linkRepository.deleteAll();
        userRepository.addUser(771L);
        Link link = linkRepository.createLink("http://jooq.test.com", "http://jooq.test.com/testdelete");
        service.addLinkTracking(771L, URI.create("http://jooq.test.com/testdelete"));

        service.removeLinkTracking(771L, URI.create("http://jooq.test.com/testdelete"));

        assertThat(linkRepository.findUserLinks(771L).isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Remove non-existing link tracking test")
    public void removeNonExistingLinkTrackingTest() {
        assertThatThrownBy(() ->
            service.removeLinkTracking(717L, URI.create("http://jooq.test.com/test-delete-non-existing-link")))
            .isExactlyInstanceOf(LinkNotFoundException.class);
    }

    @Test
    @DisplayName("Remove link tracking from non-existing user test")
    public void removeLinkTrackingFromNonExistingUserTest() {
        userRepository.addUser(755L);
        service.addLinkTracking(755L, URI.create("https://jooq.test.com/test-delete-non-existing-user"));

        assertThatThrownBy(() ->
            service.removeLinkTracking(404L, URI.create("https://jooq.test.com/test-delete-non-existing-user")))
            .isExactlyInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("Get tracked links test")
    public void getTrackedLinksTest() {
        userRepository.addUser(7555L);
        Link link = linkRepository.createLink("http://jooq.test.com", "http://jooq.test.com/getTrackedLinks");
        service.addLinkTracking(7555L, URI.create(link.getUrl()));

        List<Link> links = service.getTrackedLinks(7555L);

        assertThat(links.stream().anyMatch(l -> l.getUrl().equals(link.getUrl()))).isEqualTo(true);
    }

    @Test
    @DisplayName("Get tracked links by non-existing user test")
    public void getTrackedLinksOfNonExistingUserTest() {
        List<Link> links = service.getTrackedLinks(404L);

        assertThat(links.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Get old checked links")
    public void getOldLinksTest() {
        Link oldLink = linkRepository.createLink("http://jooq.test.com", "http://jooq.test.com/old",
            OffsetDateTime.now(), null, OffsetDateTime.now().minusHours(2)
        );

        List<Link> oldLinks = service.getOldCheckedLinks(1);

        assertThat(oldLinks.stream().anyMatch(l -> l.getUrl().equals(oldLink.getUrl()))).isEqualTo(true);
    }

    @Test
    @DisplayName("Update check time test")
    public void updateLastCheckTest() {
        Link link = linkRepository.createLink("http://jooq.test.com", "http://jooq.test.com/update-check",
            OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now().minusDays(15)
        );

        service.updateLastCheckTime(link.getUrl());

        assertThat(linkRepository.findLinkByUrl(link.getUrl()).get().getLastCheck())
            .isEqualToIgnoringSeconds(OffsetDateTime.now());
    }

    @Test
    @DisplayName("Update last update time test")
    public void updateLastUpdateTest() {
        OffsetDateTime testDate = OffsetDateTime.of(1010, 10, 10, 0, 0, 0, 0, ZoneOffset.UTC);
        Link link = linkRepository.createLink("http://jooq.test.com", "http://jooq.test.com/update-last-update",
            OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now()
        );

        service.updateLastUpdateTime(link.getUrl(), testDate);

        assertThat(linkRepository.findLinkByUrl(link.getUrl()).get().getLastUpdate()).isEqualTo(testDate);
    }
}
