package edu.java.scrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class InitializationTest extends IntegrationTest {
    private Connection posrgresConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        posrgresConnection = POSTGRES.createConnection("");
    }

    @Test
    @DisplayName("Links insertion test")
    public void insertionTest() throws SQLException {
        PreparedStatement insertStatement = posrgresConnection.prepareStatement("insert into links(url, domain_name)  values (?, ?)");
        insertStatement.setString(1, "test.com/why");
        insertStatement.setString(2, "test.com");
        insertStatement.executeUpdate();

        PreparedStatement selectStatement = posrgresConnection.prepareStatement("SELECT domain_name FROM links");
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next();

        assertThat(resultSet.getString(1)).isEqualTo("test.com");
    }
}
