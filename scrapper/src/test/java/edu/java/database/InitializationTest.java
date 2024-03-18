package edu.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class InitializationTest extends IntegrationTest {
    private static Connection posrgresConnection;

    @BeforeAll
    @SneakyThrows
    public static void setUp() {
        posrgresConnection = POSTGRES.createConnection("");
    }

    @AfterAll
    @SneakyThrows
    public static void cleanUp() {
        posrgresConnection.prepareStatement("DELETE FROM links").executeUpdate();
    }

    @Test
    @DisplayName("Links insertion test")
    public void insertionTest() throws SQLException {
        PreparedStatement insertStatement =
            posrgresConnection.prepareStatement("insert into links(url, domain_name)  values (?, ?)");
        insertStatement.setString(1, "test.com/why");
        insertStatement.setString(2, "test.com");
        insertStatement.executeUpdate();

        PreparedStatement selectStatement = posrgresConnection.prepareStatement("SELECT domain_name FROM links");
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next();

        assertThat(resultSet.getString(1)).isEqualTo("test.com");
    }
}
