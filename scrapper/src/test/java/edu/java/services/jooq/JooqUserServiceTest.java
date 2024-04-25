package edu.java.services.jooq;

import edu.database.entities.Link;
import edu.database.entities.User;
import edu.java.exceptions.ApiException;
import edu.java.repositories.IntegrationTest;
import edu.java.repositories.jooq.JooqUserRepository;
import edu.java.services.UserService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = {
    "app.database-access-type=jooq",
    "spring.cache.type=none",
    "bucket4j.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
}
)
public class JooqUserServiceTest extends IntegrationTest {
    @Autowired
    private UserService service;
    @Autowired
    private JooqUserRepository repository;

    @Test
    @DisplayName("Registration test")
    public void registrationTest() {
        service.registerUser(7123L);

        Optional<User> user = repository.findUserById(7123L);

        assertThat(user).isPresent();
        assertThat(user.get().getTgId()).isEqualTo(7123L);
    }

    @Test
    @DisplayName("Double registration test")
    public void doubleRegistrationTest() {
        service.registerUser(763L);

        assertThatThrownBy(() -> service.registerUser(763L))
            .isExactlyInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("Deletion test")
    public void deletionTest() {
        User user = new User(333L);
        repository.addUser(user.getTgId());

        service.deleteUser(user.getTgId());

        assertThat(repository.findUserById(333L)).isEmpty();
    }

    @Test
    @DisplayName("Get link owner test")
    public void getLinkOwnersTest() {
        Link link = new Link(555L, "jooq.tt.ru", "jooq.tt.ru/test", null, null, null);

        assertThat(service.getUsersIdsWithLink(link)).isNotNull();
    }
}
