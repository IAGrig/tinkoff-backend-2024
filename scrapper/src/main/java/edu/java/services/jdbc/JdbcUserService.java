package edu.java.services.jdbc;

import edu.database.entities.Link;
import edu.java.exceptions.ApiException;
import edu.java.repositories.jdbc.JdbcUserRepository;
import edu.java.services.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;

@AllArgsConstructor
@Log4j2
public class JdbcUserService implements UserService {
    private JdbcUserRepository userRepository;

    @Override
    public void registerUser(Long tgChatId) {
        try {
            userRepository.addUser(tgChatId);
        } catch (DuplicateKeyException e) {
            throw new ApiException("User is already registered");
        }
    }

    @Override
    public void deleteUser(Long tgChatId) {
        userRepository.removeUser(tgChatId);
    }

    @Override
    public List<Long> getUsersIdsWithLink(Link link) {
        return userRepository.findUsersIdsWithLink(link.getUrl());
    }
}
