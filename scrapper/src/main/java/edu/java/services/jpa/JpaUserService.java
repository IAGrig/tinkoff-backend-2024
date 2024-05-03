package edu.java.services.jpa;

import edu.database.entities.Link;
import edu.database.entities.User;
import edu.java.exceptions.ApiException;
import edu.java.repositories.jpa.JpaUserRepository;
import edu.java.services.UserService;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JpaUserService implements UserService {
    private JpaUserRepository repository;

    @Override
    public void registerUser(Long tgChatId) {
        if (repository.findById(tgChatId).isPresent()) {
            throw new ApiException("This user is already registered");
        }
        repository.save(new User(tgChatId));
    }

    @Override
    public void deleteUser(Long tgChatId) {
        if (repository.findById(tgChatId).isEmpty()) {
            throw new ApiException("This user is not registered");
        }
        repository.deleteById(tgChatId);
    }

    @Override
    public List<Long> getUsersIdsWithLink(Link link) {
        return repository.findAllLinkOwners(link.getId());
    }
}
