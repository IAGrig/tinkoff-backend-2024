package edu.java.bot.bot.commands.stub;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.links.LinksHandler;

public class TrackCommandHandler extends CommandHandler {

    public TrackCommandHandler(String command, Database database, LinksHandler linksHandler) {
        super(command, database, linksHandler);
    }

    @Override
    public String executeCommand(Message message) {
        String linkUrl = null;
        try {
            linkUrl = message.text().split(" ")[1].toLowerCase();
            String domain = linksHandler.validateURLAndGetDomain(linkUrl);
            Long linkID = database.createLink(domain, linkUrl);
            database.addLinkToUser(message.from().id(), linkID);

            return "Link added to your track list.";
        } catch (IndexOutOfBoundsException e) {
            return "Please, enter your link URL in one line with /track command after whitespace.";
        } catch (IllegalArgumentException e) {
            return "Unsupported URL domain.";
        }
    }
}
