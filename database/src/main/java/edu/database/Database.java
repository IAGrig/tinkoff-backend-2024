package edu.database;

import edu.database.entities.Link;
import java.util.List;

public interface Database {
    Boolean isUserRegistered(Long userID);

    void registerUser(Long userID);

    void deleteUser(Long userID);

    void addLinkToUser(Long userID, Long linkID);

    void removeLinkFromUser(Long userID, Long linkID);

    void removeLinkFromUser(Long userID, String url);

    Long createLink(String domain, String url);

    Link getUserLink(Long userID, String url);

    List<Link> getAllUserLinks(Long userID);
}
