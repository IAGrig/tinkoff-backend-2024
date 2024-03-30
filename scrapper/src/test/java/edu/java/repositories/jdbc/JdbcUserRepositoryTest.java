package edu.java.repositories.jdbc;

import edu.database.entities.User;
import edu.database.exceptions.UserNotFoundException;
import edu.java.repositories.IntegrationTest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
public class JdbcUserRepositoryTest extends IntegrationTest {
    private static Connection posrgresConnection;
    @Autowired
    private JdbcUserRepository userRepository;

    @BeforeAll
    public static void setUp() throws SQLException {
        posrgresConnection = POSTGRES.createConnection("");
    }

    @AfterAll
    public static void cleanUp() throws SQLException {
        posrgresConnection.prepareStatement("DELETE From links;").executeUpdate();
        posrgresConnection.prepareStatement("DELETE From users;").executeUpdate();
        posrgresConnection.prepareStatement("DELETE From users_links;").executeUpdate();
    }

    @Test
    @DisplayName("User creation test")
    public void addTest() throws SQLException {
        PreparedStatement selectStatement = posrgresConnection
            .prepareStatement("SELECT tg_id, registered FROM users;");

        User user = userRepository.addUser(123L);
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next();

        assertThat(user).isNotNull();
        assertThat(resultSet.getLong(1)).isEqualTo(123L);
        assertThat(offsetDateTimeFromTimestamp(resultSet.getTimestamp(2)))
            .isEqualToIgnoringSeconds(OffsetDateTime.now());
    }

    @Test
    @DisplayName("User deletion test")
    public void removeTest() throws SQLException {
        PreparedStatement selectStatement = posrgresConnection
            .prepareStatement("SELECT tg_id, registered FROM users WHERE tg_id = ?;");
        selectStatement.setLong(1, 321L);

        userRepository.addUser(321L);
        User user = userRepository.removeUser(321L);
        ResultSet resultSet = selectStatement.executeQuery();

        assertThat(user).isNotNull();
        assertThat(resultSet.next()).isEqualTo(false);
    }

    @Test
    @DisplayName("Non existing user deletion test")
    public void removeNonExistingUserTest() throws SQLException {
        assertThatThrownBy(() -> userRepository.removeUser(404L))
            .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("User find all test")
    public void findAllTest() throws SQLException {
        PreparedStatement insertQuery = posrgresConnection
            .prepareStatement("INSERT INTO users(tg_id) VALUES (111);");
        insertQuery.executeUpdate();

        List<User> users = userRepository.findAll();

        assertThat(users.size()).isGreaterThanOrEqualTo(1);
        assertThat(users.stream().anyMatch(user -> user.getTgId().equals(111L))).isEqualTo(true);
    }

    @Test
    @DisplayName("User find by id test")
    public void findByIdTest() throws SQLException {
        PreparedStatement insertQuery = posrgresConnection
            .prepareStatement("INSERT INTO users(tg_id) VALUES (121);");
        insertQuery.executeUpdate();

        User user = userRepository.findUserById(121L);

        assertThat(user).isNotNull();
        assertThat(user.getTgId()).isEqualTo(121L);
    }

    @Test
    @DisplayName("User find by non-existing id test")
    public void findByIdNonExistingTest() throws SQLException {
        assertThatThrownBy(() -> userRepository.findUserById(404L))
            .isExactlyInstanceOf(UserNotFoundException.class);
    }

    private OffsetDateTime offsetDateTimeFromTimestamp(Timestamp timestamp) {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp.getTime()), ZoneId.systemDefault());
    }
}
