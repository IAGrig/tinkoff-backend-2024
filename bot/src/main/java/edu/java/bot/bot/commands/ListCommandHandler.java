package edu.java.bot.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.database.entities.Link;
import edu.java.bot.bot.links.LinksHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListCommandHandler extends CommandHandler {

    public ListCommandHandler(String command, Database database, LinksHandler linksHandler) {
        super(command, database, linksHandler);
    }

    @Override
    public String executeCommand(Message message) {
        Long userID = message.from().id();
        List<Link> userLinks = database.getAllUserLinks(userID);
        if (userLinks.isEmpty()) {
            return "You don't track any links.";
        }

        Map<String, List<Link>> linksByDomains = groupLinksByDomains(userLinks);
        StringBuilder stringBuilder = new StringBuilder("Here are all your links:\n");
        for (String domain : linksByDomains.keySet()) {
            stringBuilder.append(String.format("[%s]:\n", domain));
            for (Link link : linksByDomains.get(domain)) {
                stringBuilder.append("    ").append(link.url()).append("\n");
            }
        }

        return stringBuilder.toString();
    }

    private Map<String, List<Link>> groupLinksByDomains(List<Link> userLinks) {
        HashMap<String, List<Link>> map = new HashMap<>();

        for (Link link : userLinks) {
            if (!map.containsKey(link.domain())) {
                map.put(link.domain(), new ArrayList<>() {
                });
            }
            map.get(link.domain()).add(link);
        }

        return map;
    }
}
