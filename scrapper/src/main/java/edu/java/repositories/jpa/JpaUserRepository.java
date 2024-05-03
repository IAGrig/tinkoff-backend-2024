package edu.java.repositories.jpa;

import edu.database.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {
    @Query("select u.tgId from User u join u.links l where l.id = :linkId")
    List<Long> findAllLinkOwners(Long linkId);
}
