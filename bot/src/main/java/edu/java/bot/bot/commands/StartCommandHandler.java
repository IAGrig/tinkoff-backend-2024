package edu.java.bot.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.links.LinksHandler;

public class StartCommandHandler extends CommandHandler {

    public StartCommandHandler(String command, Database database, LinksHandler linksHandler) {
        super(command, database, linksHandler);
    }

    @Override
    public String executeCommand(Message message) {
        return String.format(
            "Welcome, %s. Try /help command to discover all functionality.",
            message.from().firstName()
        );
    }
}
