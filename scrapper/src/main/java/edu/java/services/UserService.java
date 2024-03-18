package edu.java.services;

import edu.database.entities.Link;
import java.util.List;

public interface UserService {
    void registerUser(Long tgChatId);

    void deleteUser(Long tgChatId);

    List<Long> getUsersIdsWithLink(Link link);
}
