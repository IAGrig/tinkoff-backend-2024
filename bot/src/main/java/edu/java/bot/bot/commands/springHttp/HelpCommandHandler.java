package edu.java.bot.bot.commands.springHttp;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.bot.links.LinksHandler;
import edu.java.bot.httpClients.ScrapperHttpClient;

public class HelpCommandHandler extends CommandHandler {
    private final static String HELP_MESSAGE = """
        Welcome to Link Updates Notifier. \n
        Here is list of all available commands:
        /start - register in this bot.
        /help - show this message.
        /list - show list of tracking links.
        /track <link> - start to track <link>.
        /untrack <url> - end tracking of link with specified url.
        """;

    public HelpCommandHandler(String command, ScrapperHttpClient scrapperClient, LinksHandler linksHandler) {
        super(command, scrapperClient, linksHandler);
    }

    @Override
    public String executeCommand(Message message) {
        return HELP_MESSAGE;
    }
}
