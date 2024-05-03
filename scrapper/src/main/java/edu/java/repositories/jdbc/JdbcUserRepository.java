package edu.java.repositories.jdbc;

import edu.database.entities.User;
import edu.database.exceptions.UserNotFoundException;
import edu.java.exceptions.ApiException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.repositories.jdbc.UserMapUtils.userFromKeyHolder;

@SuppressWarnings("MultipleStringLiterals")
@AllArgsConstructor
@Repository
public class JdbcUserRepository {
    private JdbcClient jdbcClient;

    @Transactional
    public User addUser(Long userId) {
        String addQuery = "INSERT INTO users(tg_id) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int affectedRows = jdbcClient.sql(addQuery)
            .param(userId)
            .update(keyHolder);
        if (affectedRows == 0) {
            throw new ApiException("User creation failed");
        }
        return userFromKeyHolder(keyHolder);
    }

    public User removeUser(Long userId) {
        String request = "DELETE FROM users WHERE tg_id = ?";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int affectedRows = jdbcClient.sql(request)
            .param(userId)
            .update(keyHolder);

        if (affectedRows == 0) {
            throw new UserNotFoundException(String.format("User with id=%d not found", userId));
        }
        return userFromKeyHolder(keyHolder);
    }

    public List<User> findAll() {
        String request = "SELECT tg_id, registered FROM users";
        return jdbcClient.sql(request)
            .query(new UserMapUtils())
            .list();
    }

    public User findUserById(Long userId) {
        String selectQuery = "SELECT tg_id, registered FROM users WHERE tg_id = ?";
        try {
            return jdbcClient.sql(selectQuery)
                .param(userId)
                .query(new UserMapUtils())
                .single();
        } catch (EmptyResultDataAccessException ex) {
            throw new UserNotFoundException(String.format("User with id=%d not found", userId));
        }
    }

    public List<Long> findUsersIdsWithLink(String url) {
        String request = "SELECT user_tg_id FROM users_links ul "
            + "JOIN links l ON ul.link_id = l.id "
            + "where l.url = ?";
        return jdbcClient.sql(request)
            .param(url)
            .query()
            .singleColumn()
            .stream()
            .map(el -> (Long) el)
            .toList();
    }
}
