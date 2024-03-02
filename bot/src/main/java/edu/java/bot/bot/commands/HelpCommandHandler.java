package edu.java.bot.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.links.LinksHandler;

public class HelpCommandHandler extends CommandHandler {
    private final static String HELP_MESSAGE = """
        Welcome to Link Updates Notifier. \n
        Here is list of all available commands:
        /start - register in this bot.
        /help - show this message.
        /list - show list of tracking links.
        /track <link> - start to track <link>.
        /untrack <id> - end tracking of link with id=<id>.
        """;

    public HelpCommandHandler(String command, Database database, LinksHandler linksHandler) {
        super(command, database, linksHandler);
    }

    @Override
    public String executeCommand(Message message) {
        return HELP_MESSAGE;
    }
}
