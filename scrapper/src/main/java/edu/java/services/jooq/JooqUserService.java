package edu.java.services.jooq;

import edu.database.entities.Link;
import edu.java.repositories.jooq.JooqUserRepository;
import edu.java.services.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JooqUserService implements UserService {
    private final JooqUserRepository repository;

    @Override
    public void registerUser(Long tgChatId) {
        repository.addUser(tgChatId);
    }

    @Override
    public void deleteUser(Long tgChatId) {
        repository.removeUser(tgChatId);
    }

    @Override
    public List<Long> getUsersIdsWithLink(Link link) {
        return repository.findUsersIdsWithLink(link.getUrl());
    }
}
