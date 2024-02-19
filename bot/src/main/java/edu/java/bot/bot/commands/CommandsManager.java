package edu.java.bot.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.links.LinksHandler;

public class CommandsManager {
    private final LinksHandler linksHandler;
    private final CommandHandler firstHandler;
    private final Database database;

    public CommandsManager(Database database, LinksHandler linksHandler) {
        this.database = database;
        this.linksHandler = linksHandler;

        CommandHandler start = new StartCommandHandler("/start", database, linksHandler);
        CommandHandler help = new HelpCommandHandler("/help", database, linksHandler);
        CommandHandler track = new TrackCommandHandler("/track", database, linksHandler);
        CommandHandler untrack = new UntrackCommandHandler("/untrack", database, linksHandler);
        CommandHandler list = new ListCommandHandler("/list", database, linksHandler);

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
