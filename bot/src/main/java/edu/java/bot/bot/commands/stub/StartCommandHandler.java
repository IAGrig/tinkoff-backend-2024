package edu.java.bot.bot.commands.stub;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.links.LinksHandler;

public class StartCommandHandler extends CommandHandler {

    public StartCommandHandler(String command, Database database, LinksHandler linksHandler) {
        super(command, database, linksHandler);
    }

    @Override
    public String executeCommand(Message message) {
        Long userID = message.from().id();
        boolean isUserRegistered = database.isUserRegistered(userID);
        if (!isUserRegistered) {
            database.registerUser(userID);
            return String.format(
                "Welcome, %s. Try /help command to discover all functionality.",
                message.from().firstName()
            );
        }
        return "Sorry, you are already registered.";

    }
}
