package edu.java.services.jpa;

import edu.database.entities.Link;
import edu.database.entities.User;
import edu.java.exceptions.ApiException;
import edu.java.repositories.IntegrationTest;
import edu.java.repositories.jpa.JpaUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
public class JpaUserServiceTest extends IntegrationTest {
    @Autowired
    private JpaUserService service;
    @Autowired
    private JpaUserRepository repository;

    @Test
    @DisplayName("Registration test")
    public void registrationTest() {
        service.registerUser(123L);

        User user = repository.findById(123L).orElse(null);

        assertThat(user).isNotNull();
        assertThat(user.getTgId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("Double registration test")
    public void doubleRegistrationTest() {
        service.registerUser(153L);

        assertThatThrownBy(() -> service.registerUser(153L))
            .isExactlyInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("Deletion test")
    public void deletionTest() {
        User user = new User(333L);
        repository.save(user);

        service.deleteUser(user.getTgId());

        assertThat(repository.findById(333L).isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("Get link owner test")
    public void getLinkOwnersTest() {
        Link link = new Link(555L, "tt.ru", "tt.ru/test", null, null, null);

        assertThat(service.getUsersIdsWithLink(link)).isNotNull();
    }
}
