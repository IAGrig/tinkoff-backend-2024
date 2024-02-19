package edu.database;

import edu.database.entities.Link;
import java.util.List;

public interface Database {
    void addLinkToUser(Long userID, Long linkID);

    void removeLinkFromUser(Long userID, Long linkID);

    Long createLink(String domain, String url);

    List<Link> getAllUserLinks(Long userID);
}
