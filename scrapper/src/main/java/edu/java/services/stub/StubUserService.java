package edu.java.services.stub;

import edu.database.Database;
import edu.database.entities.Link;
import edu.java.services.UserService;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StubUserService implements UserService {
    private Database database;

    public void registerUser(Long userId) {
        if (database.isUserRegistered(userId)) {
            throw new IllegalArgumentException("The user is already registered.");
        }
        database.registerUser(userId);
    }

    public void deleteUser(Long userId) {
        database.deleteUser(userId);
    }

    @Override
    public List<Long> getUsersIdsWithLink(Link link) {
        // please, use real db implementation
        return List.of();
    }
}
