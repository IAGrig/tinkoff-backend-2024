package edu.database;

import edu.database.entities.Link;
import edu.database.exceptions.LinkNotFoundException;
import edu.database.exceptions.UserNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class StubDatabase implements Database {
    private final Map<Long, Link> linkMap;
    private final Map<Long, List<Long>> usersToLinksMap;
    private final Set<Long> registeredUsers;
    private Long availableID = 1L;

    @Override
    public Boolean isUserRegistered(Long userID) {
        Boolean result = registeredUsers.contains(userID);
        log.info(String.format("User %d registration is checked: %b", userID, result));
        return result;
    }

    @Override
    public void registerUser(Long userID) {
        registeredUsers.add(userID);
        log.info("User %s has registered".formatted(userID.toString()));
    }

    @Override
    public void deleteUser(Long userID) {
        registeredUsers.remove(userID);
        log.info("User %s has removed".formatted(userID.toString()));
    }

    @Override
    public void addLinkToUser(Long userID, Long linkID) {
        if (!usersToLinksMap.containsKey(userID)) {
            usersToLinksMap.put(userID, new ArrayList<>());
        }
        usersToLinksMap.get(userID).add(linkID);
        log.info(String.format("Link %d added to user %d", linkID, userID));
    }

    @Override
    public void removeLinkFromUser(Long userID, Long linkID) {
        if (!usersToLinksMap.containsKey(userID)) {
            usersToLinksMap.put(userID, new ArrayList<>());
        }
        usersToLinksMap.get(userID).remove(linkID);
        log.info(String.format("Link %d removed from user %d", linkID, userID));
    }

    @Override
    public void removeLinkFromUser(Long userID, String url) {
        if (!usersToLinksMap.containsKey(userID)) {
            usersToLinksMap.put(userID, new ArrayList<>());
        }
        Link link = getLinkByUrl(url);
        if (link == null) {
            throw new LinkNotFoundException("The link with the specified url was not found.");
        }
        usersToLinksMap.get(userID).remove(link.id());
        log.info(String.format("Link %s removed from user %d", url, userID));

    }

    @Override
    public Long createLink(String domain, String url) {
        Link link = new Link(availableID, domain, url);
        linkMap.put(availableID, link);
        log.info(String.format("Created link with id=%d and URL=%s", availableID, url));
        return availableID++;
    }

    @Override
    public Link getUserLink(Long userID, String url) {
        if (!registeredUsers.contains(userID)) {
            throw new UserNotFoundException("The user was not found");
        }
        Link link = getLinkByUrl(url);
        if (link == null) {
            throw new LinkNotFoundException("The link was not found");
        }
        return link;
    }

    @Override
    public List<Link> getAllUserLinks(Long userID) {
        List<Long> userLinksIDs = usersToLinksMap.entrySet().stream()
            .filter(entry -> entry.getKey().equals(userID))
            .flatMap(entry -> entry.getValue().stream()).toList();
        List<Link> result = linkMap.values().stream()
            .filter(link -> userLinksIDs.contains(link.id()))
            .toList();
        log.info(String.format("Got links for user %d", userID));
        return result;
    }

    private Link getLinkByUrl(String url) {
        List<Link> linksWithUrl = linkMap.values().stream()
            .filter(link -> link.url().equals(url))
            .toList();
        if (linksWithUrl.isEmpty()) {
            return null;
        }
        return linksWithUrl.getFirst();
    }
}
