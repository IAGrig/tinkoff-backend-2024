package edu.java.bot.bot.commands.stub;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.links.LinksHandler;

public class UntrackCommandHandler extends CommandHandler {

    public UntrackCommandHandler(String command, Database database, LinksHandler linksHandler) {
        super(command, database, linksHandler);
    }

    @Override
    public String executeCommand(Message message) {
        Long userID = message.from().id();
        Long linkID = null;
        try {
            String[] splittedMessage = message.text().split(" ");
            linkID = Long.parseLong(splittedMessage[1]);
        } catch (IndexOutOfBoundsException e) {
            return "Please, enter link ID in one message with /untrack command after whitespace.";
        } catch (NumberFormatException e) {
            return "Link ID must be a number.";
        }
        database.removeLinkFromUser(userID, linkID);
        return String.format("You stopped tracking link with id=%d.", linkID);
    }

}
