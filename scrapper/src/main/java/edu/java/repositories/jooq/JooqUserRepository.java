package edu.java.repositories.jooq;

import edu.database.entities.User;
import edu.database.exceptions.UserNotFoundException;
import edu.java.exceptions.ApiException;
import edu.java.scrapper.domain.jooq.tables.Links;
import edu.java.scrapper.domain.jooq.tables.Users;
import edu.java.scrapper.domain.jooq.tables.UsersLinks;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class JooqUserRepository {
    private final DSLContext dsl;

    public User addUser(Long userId) {
        try {
            return dsl.insertInto(Users.USERS)
                .columns(Users.USERS.TG_ID)
                .values(userId)
                .returning()
                .fetchOneInto(User.class);
        } catch (DuplicateKeyException | IntegrityConstraintViolationException ex) {
            throw new ApiException("The user is already registered.");
        }
    }

    public User removeUser(Long userId) {
        return dsl.deleteFrom(Users.USERS)
            .where(Users.USERS.TG_ID.eq(userId))
            .returning()
            .fetchOptional()
            .orElseThrow(() -> new UserNotFoundException("User with id=%d not found".formatted(userId)))
            .into(User.class);
    }

    public List<User> findAll() {
        return dsl.selectFrom(Users.USERS)
            .fetchInto(User.class);
    }

    public Optional<User> findUserById(Long userId) {
        return dsl.selectFrom(Users.USERS)
            .where(Users.USERS.TG_ID.eq(userId))
            .fetchOptional()
            .map(userRecord -> userRecord.into(User.class));
    }

    public List<Long> findUsersIdsWithLink(String url) {
        Integer linkId = dsl.select(Links.LINKS.ID)
            .from(Links.LINKS)
            .where(Links.LINKS.URL.eq(url))
            .fetchOneInto(Integer.class);

        return dsl.select(UsersLinks.USERS_LINKS.USER_TG_ID)
            .from(UsersLinks.USERS_LINKS)
            .where(UsersLinks.USERS_LINKS.LINK_ID.eq(linkId))
            .fetchInto(Long.class);
    }
}
