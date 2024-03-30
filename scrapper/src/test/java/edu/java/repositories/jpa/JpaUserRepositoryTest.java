package edu.java.repositories.jpa;

import edu.database.entities.User;
import edu.java.repositories.IntegrationTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class JpaUserRepositoryTest extends IntegrationTest {
    @Autowired
    private JpaUserRepository userRepository;

    @Test
    @DisplayName("User creation test")
    public void addTest() {
        userRepository.deleteAll();
        User user = new User(123L);
        Optional<User> beforeAddition = userRepository.findById(123L);
        userRepository.save(user);

        assertThat(beforeAddition.isPresent()).isEqualTo(false);
        assertThat(userRepository.findById(123L).isPresent()).isEqualTo(true);
    }

    @Test
    @DisplayName("User deletion test")
    public void removeTest() {
        User user = new User(111L); // already tested
        userRepository.save(user);

        userRepository.deleteById(111L);

        assertThat(userRepository.findById(111L).isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("Non existing user deletion test")
    public void removeNonExistingUserTest() {
        assertDoesNotThrow(() -> userRepository.deleteById(404L));
    }

    @Test
    @DisplayName("User find all test")
    public void findAllTest() {
        userRepository.deleteAll();
        List<User> users = List.of(
            new User(1231L),
            new User(1232L),
            new User(1233L)
        );
        userRepository.saveAll(users);

        List<User> usersFromDB = userRepository.findAll();

        assertThat(usersFromDB).isNotNull();
        assertThat(usersFromDB.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("User find by non-existing id test")
    public void findByIdNonExistingTest() {
        assertThat(userRepository.findById(404L).isPresent()).isEqualTo(false);
    }
}
