package edu.java.repositories.jpa;

import edu.database.entities.Link;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface JpaLinkRepository extends JpaRepository<Link, Long> {
    Link findByUrl(String url);

    @Query(value = "SELECT * FROM links WHERE last_check + interval '?1 hours' < now();", nativeQuery = true)
    @Transactional
    List<Link> findAllNotCheckedFor(int hours);

    @Modifying
    @Query(value = "INSERT INTO users_links (user_tg_id, link_id) VALUES (?1, ?2)", nativeQuery = true)
    @Transactional
    void addChatTracking(Long userTgId, Long linkId);

    @Modifying
    @Query(value = "DELETE FROM users_links WHERE user_tg_id = ?1 AND link_id = ?2", nativeQuery = true)
    @Transactional
    void removeLinkTracking(Long userTgId, Long linkId);

    @Query("select l from Link l join l.users u where u.tgId = ?1")
    List<Link> findAllUserLinks(Long userTgId);
}
