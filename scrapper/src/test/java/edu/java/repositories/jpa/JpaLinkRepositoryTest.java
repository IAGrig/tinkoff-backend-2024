package edu.java.repositories.jpa;

import edu.database.entities.Link;
import edu.java.repositories.IntegrationTest;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class JpaLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private JpaLinkRepository linkRepository;

    @Test
    @DisplayName("Link creation test")
    public void addTest() {
        linkRepository.deleteAll();
        Link link = getTestLink("test");

        Link addedLink = linkRepository.save(link);

        assertThat(linkRepository.findById(addedLink.getId()).isPresent()).isEqualTo(true);
    }

    @Test
    @DisplayName("Link deletion test")
    public void removeTest() {
        Link link = linkRepository.save(getTestLink("test"));

        linkRepository.deleteById(link.getId());

        assertThat(linkRepository.findById(link.getId()).isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("Non existing link deletion test")
    public void removeNonExistingLinkTest() {
        assertDoesNotThrow(() -> linkRepository.deleteById(404L));
    }

    @Test
    @DisplayName("Link find all test")
    public void findAllTest() {
        linkRepository.deleteAll();
        List<Link> links = List.of(
            getTestLink("1"),
            getTestLink("2"),
            getTestLink("3")
        );
        links.forEach(System.out::println);
        linkRepository.saveAll(links);

        List<Link> actual = linkRepository.findAll();

        assertThat(actual).isNotNull();
        assertThat(actual.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Find link by non-existing id test")
    public void findByIdNonExistingTest() {
        assertThat(linkRepository.findById(404L).isPresent()).isEqualTo(false);
    }

    private Link getTestLink(String urlPart) {
        return new Link(null, "test.com", "test.com/" + urlPart,
            OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now()
        );
    }
}
