package edu.java.repositories.jdbc;

import edu.database.entities.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;
import static edu.java.repositories.jdbc.LinkMapUtils.offsetDateTimeFromSql;

@SuppressWarnings("MultipleStringLiterals")
public class UserMapUtils implements RowMapper<User> {
    public static User userFromKeyHolder(KeyHolder keyHolder) {
        Long userId = Long.parseLong(keyHolder.getKeys().get("tg_id").toString());
        OffsetDateTime registered = offsetDateTimeFromSql((Timestamp) keyHolder.getKeys().get("registered"));
        return new User(userId, registered);
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long userId = rs.getLong("tg_id");
        OffsetDateTime registered = offsetDateTimeFromSql(rs.getTimestamp("registered"));
        return new User(userId, registered);
    }
}
