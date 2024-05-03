package edu.java.repositories.jdbc;

import edu.database.entities.Link;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

@SuppressWarnings("MultipleStringLiterals")
public class LinkMapUtils implements RowMapper<Link> {
    public static OffsetDateTime offsetDateTimeFromSql(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp.getTime()), ZoneId.systemDefault());
    }

    public static Link linkFromKeyHolder(KeyHolder keyHolder) {
        Long id = Long.parseLong(keyHolder.getKeys().get("id").toString());
        String domain = (String) keyHolder.getKeys().get("domain_name");
        String url = (String) keyHolder.getKeys().get("url");
        OffsetDateTime registered = offsetDateTimeFromSql((Timestamp) keyHolder.getKeys().get("registered"));
        OffsetDateTime lastUpdate = offsetDateTimeFromSql((Timestamp) keyHolder.getKeys().get("last_update"));
        OffsetDateTime lastCheck = offsetDateTimeFromSql((Timestamp) keyHolder.getKeys().get("last_check"));

        return new Link(id, domain, url, registered, lastUpdate, lastCheck);
    }

    @Override
    public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("ID");
        String domain = rs.getString("domain_name");
        String url = rs.getString("url");
        OffsetDateTime registered = offsetDateTimeFromSql(rs.getTimestamp("registered"));
        OffsetDateTime lastUpdate = offsetDateTimeFromSql(rs.getTimestamp("last_update"));
        OffsetDateTime lastCheck = offsetDateTimeFromSql(rs.getTimestamp("last_check"));

        return new Link(id, domain, url, registered, lastUpdate, lastCheck);
    }
}
