package edu.java.database.jdbc;

import edu.database.entities.Link;
import edu.database.exceptions.LinkNotFoundException;
import edu.database.exceptions.UserNotFoundException;
import edu.java.database.IntegrationTest;
import edu.java.exceptions.ApiException;
import edu.java.repositories.jdbc.JdbcLinkRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
public class JdbcLinkRepositoryTest extends IntegrationTest {
    private static Connection posrgresConnection;
    @Autowired
    private JdbcLinkRepository linkRepository;

    @SneakyThrows
    @BeforeAll
    public static void setUp() {
        posrgresConnection = POSTGRES.createConnection("");
        // add test user
        PreparedStatement addUserStatement = posrgresConnection
            .prepareStatement("INSERT INTO users(tg_id) VALUES (123);");
        addUserStatement.executeUpdate();
    }

    @AfterAll
    public static void cleanUp() throws SQLException {
        posrgresConnection.prepareStatement("DELETE From links;").executeUpdate();
        posrgresConnection.prepareStatement("DELETE From users;").executeUpdate();
        posrgresConnection.prepareStatement("DELETE From users_links;").executeUpdate();
    }

    @Test
    @DisplayName("Link adding test")
    public void addTest() throws SQLException {
        PreparedStatement selectStatement = posrgresConnection
            .prepareStatement("SELECT id, url, domain_name, last_update, last_check FROM links;");

        Link link = linkRepository.addLinkToUser("test.com", "test.com/add", 123L);
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next();

        assertThat(link).isNotNull();
        assertThat(resultSet.getString(2)).isEqualTo("test.com/add");
        assertThat(offsetDateTimeFromTimestamp(resultSet.getTimestamp(5)))
            .isEqualToIgnoringSeconds(OffsetDateTime.now());
    }

    @Test
    @DisplayName("Link adding test for non-existing user")
    public void addForNonExistingUserTest() {
        assertThatThrownBy(() -> linkRepository.addLinkToUser("test.com", "test.com/add-non-exist", 999L))
            .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Link removing test")
    public void removeTest() throws SQLException {
        linkRepository.addLinkToUser("test.com", "test.com/delete", 123L);
        PreparedStatement selectStatement = posrgresConnection
            .prepareStatement("SELECT link_id, user_tg_id FROM users_links " +
                "WHERE user_tg_id = ? AND link_id = ?;");
        selectStatement.setLong(1, 123L);

        Link link = linkRepository.removeLinkFromUser("test.com/delete", 123L);
        selectStatement.setLong(2, link.getId());
        ResultSet resultSet = selectStatement.executeQuery();

        assertThat(link).isNotNull();
        assertThat(resultSet.next()).isEqualTo(false);
    }

    @Test
    @DisplayName("Link removing test for non-existing user")
    public void removeForNonExistingUserTest() throws SQLException {
        linkRepository.createLink("test.com", "test.com/remove-non-exist(user)");
        assertThatThrownBy(() -> linkRepository.removeLinkFromUser("test.com/remove-non-exist(user)", 999L))
            .isExactlyInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("Find user links test")
    public void findUserLinksTest() throws SQLException {
        // already tested
        linkRepository.addLinkToUser("test.com", "test.com/find-user-link", 123L);
        linkRepository.addLinkToUser("test.com", "test.com/find-user-link-2", 123L);

        List<Link> users = linkRepository.findUserLinks(123L);

        assertThat(users.size()).isGreaterThanOrEqualTo(2);
        assertThat(users.stream().anyMatch(link -> link.getUrl().equals("test.com/find-user-link")))
            .isEqualTo(true);
        assertThat(users.stream().anyMatch(link -> link.getUrl().equals("test.com/find-user-link-2")))
            .isEqualTo(true);
    }

    @Test
    @DisplayName("Find non-existing user links test")
    public void findNonExistingUserTest() throws SQLException {
        assertThatThrownBy(() -> linkRepository.findUserLinks(999L))
            .isExactlyInstanceOf(LinkNotFoundException.class);
    }

    @Test
    @DisplayName("Link find by url test")
    public void findByUrlTest() throws SQLException {
        // already tested
        linkRepository.addLinkToUser("test.com", "test.com/find-by-url", 123L);

        Link link = linkRepository.findLinkByUrl("test.com/find-by-url");

        assertThat(link).isNotNull();
        assertThat(link.getUrl()).isEqualTo("test.com/find-by-url");
        assertThat(link.getDomain()).isEqualTo("test.com");
    }

    @Test
    @DisplayName("Link find by non-existing url test")
    public void findByNonExistingUrlTest() throws SQLException {
        assertThatThrownBy(() -> linkRepository.findLinkByUrl("test.com/find-by-non-existing-url"))
            .isExactlyInstanceOf(LinkNotFoundException.class);
    }

    private OffsetDateTime offsetDateTimeFromTimestamp(Timestamp timestamp) {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp.getTime()), ZoneId.systemDefault());
    }
}
