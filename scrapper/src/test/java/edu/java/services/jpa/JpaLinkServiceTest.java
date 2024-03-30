package edu.java.services.jpa;

import edu.database.entities.Link;
import edu.database.entities.User;
import edu.java.exceptions.ApiException;
import edu.java.repositories.IntegrationTest;
import edu.java.repositories.jpa.JpaLinkRepository;
import edu.java.repositories.jpa.JpaUserRepository;
import edu.java.services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = "app.database-access-type=jpa")
public class JpaLinkServiceTest extends IntegrationTest {
    @Autowired
    private LinkService service;
    @Autowired
    private JpaLinkRepository repository;
    @Autowired
    private JpaUserRepository userRepository;

    @BeforeAll
    public static void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    @DisplayName("Add link tracking test")
    public void addLinkTrackingTest() {
        repository.deleteAll();
        userRepository.save(new User(2L));
        Link link = repository.save(new Link(null, "test.com", "test.com/test",
            OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now()
        ));

        service.addLinkTracking(2L, URI.create("test.com/test"));

        assertThat(repository.findAllUserLinks(2L)
            .stream().anyMatch(l -> Objects.equals(l.getUrl(), "test.com/test")))
            .isEqualTo(true);
    }

    @Test
    @DisplayName("Add already tracked link test")
    public void addTrackedLinkTrackingTest() {
        repository.deleteAll();
        userRepository.save(new User(2L));
        Link link = repository.save(new Link(null, "test.com", "test.com/test",
            OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now()
        ));

        service.addLinkTracking(2L, URI.create("test.com/test"));

        assertThatThrownBy(() -> service.addLinkTracking(2L, URI.create("test.com/test")))
            .isExactlyInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("Add non-existing link tracking test")
    public void addNonExistingLinkTrackingTest() {
        repository.deleteAll();
        userRepository.save(new User(22L));

        service.addLinkTracking(22L, URI.create("https://test.com/non-exist"));

        assertThat(repository.findAllUserLinks(22L)
            .stream().anyMatch(l -> l.getUrl().equals("https://test.com/non-exist")))
            .isEqualTo(true);
    }

    @Test
    @DisplayName("Remove tracking test")
    public void removeTrackingTest() {
        repository.deleteAll();
        userRepository.save(new User(1L));
        Link link = repository.save(new Link(null, "test.com", "test.com/testdelete",
            OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now()
        ));
        service.addLinkTracking(1L, URI.create("test.com/testdelete"));

        service.removeLinkTracking(1L, URI.create("test.com/testdelete"));

        assertThat(repository.findAllUserLinks(1L)
            .stream().anyMatch(l -> l.getUrl().equals("test.com/testdelete")))
            .isEqualTo(false);
    }

    @Test
    @DisplayName("Remove non-existing link tracking test")
    public void removeNonExistingLinkTrackingTest() {
        assertThatThrownBy(() ->
            service.removeLinkTracking(1L, URI.create("test.com/test-delete-non-existing-link")))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Remove link tracking from non-existing user test")
    public void removeLinkTrackingFromNonExistingUserTest() {
        userRepository.save(new User(55L));
        service.addLinkTracking(55L, URI.create("https://test.com/test-delete-non-existing-user"));

        Assertions.assertDoesNotThrow(() ->
            service.removeLinkTracking(404L, URI.create("https://test.com/test-delete-non-existing-user")));
    }

    @Test
    @DisplayName("Get tracked links test")
    public void getTrackedLinksTest() {
        userRepository.save(new User(555L));
        Link link = repository.save(new Link(null, "test.com", "test.com/getTrackedLinks",
            OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now()
        ));
        service.addLinkTracking(555L, URI.create(link.getUrl()));

        List<Link> links = service.getTrackedLinks(555L);

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
        Link oldLink = repository.save(new Link(null, "test.com", "test.com/old",
            OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now().minusHours(2)
        ));

        List<Link> oldLinks = service.getOldCheckedLinks(1);

        assertThat(oldLinks.stream().anyMatch(l -> l.getUrl().equals(oldLink.getUrl()))).isEqualTo(true);
    }

    @Test
    @DisplayName("Update check time test")
    public void updateLastCheckTest() {
        Link link = repository.save(new Link(null, "test.com", "test.com/update-check",
            OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now().minusDays(15)
        ));

        service.updateLastCheckTime(link.getUrl());

        assertThat(repository.findById(link.getId()).get().getLastCheck())
            .isEqualToIgnoringSeconds(OffsetDateTime.now());
    }

    @Test
    @DisplayName("Update last update time test")
    public void updateLastUpdateTest() {
        OffsetDateTime testDate = OffsetDateTime.of(1010, 10, 10, 0, 0, 0, 0, ZoneOffset.UTC);
        Link link = repository.save(new Link(null, "test.com", "test.com/update-last-update",
            OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now()
        ));

        service.updateLastUpdateTime(link.getUrl(), testDate);

        assertThat(repository.findById(link.getId()).get().getLastUpdate()).isEqualTo(testDate);
    }
}
