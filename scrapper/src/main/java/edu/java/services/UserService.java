package edu.java.services;

import edu.database.Database;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {
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
}
