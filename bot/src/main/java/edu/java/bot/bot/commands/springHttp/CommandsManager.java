package edu.java.bot.bot.commands.springHttp;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.bot.links.LinksHandler;
import edu.java.bot.httpClients.ScrapperHttpClient;

public class CommandsManager {
    private final CommandHandler firstHandler;

    public CommandsManager(ScrapperHttpClient scrapperClient, LinksHandler linksHandler) {
        CommandHandler start = new StartCommandHandler("/start", scrapperClient, linksHandler);
        CommandHandler help = new HelpCommandHandler("/help", scrapperClient, linksHandler);
        CommandHandler track = new TrackCommandHandler("/track", scrapperClient, linksHandler);
        CommandHandler untrack = new UntrackCommandHandler("/untrack", scrapperClient, linksHandler);
        CommandHandler list = new ListCommandHandler("/list", scrapperClient, linksHandler);

        firstHandler = start;

        start.setNextHandler(help);
        help.setNextHandler(track);
        track.setNextHandler(untrack);
        untrack.setNextHandler(list);
    }

    public String handleCommand(Message message) {
        return firstHandler.handleCommand(message);
    }
}
