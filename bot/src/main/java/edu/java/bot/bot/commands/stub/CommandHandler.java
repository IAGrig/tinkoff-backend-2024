package edu.java.bot.bot.commands.stub;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.links.LinksHandler;

public abstract class CommandHandler {
    private final String command;
    protected Database database;
    protected LinksHandler linksHandler;
    private CommandHandler nextHandler;

    public CommandHandler(String command, Database database, LinksHandler linksHandler) {
        this.command = command;
        this.database = database;
        this.linksHandler = linksHandler;
    }

    public void setNextHandler(CommandHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public String handleCommand(Message message) {
        if (!isMessageCorrect(message)) {
            return "I can't handle it.";
        }
        if (message.text().startsWith(this.command)) {
            return executeCommand(message);
        } else if (nextHandler != null) {
            return nextHandler.handleCommand(message);
        }
        return "It is not command. Try to use /help command.";
    }

    private boolean isMessageCorrect(Message message) {
        if (message.text() == null) {
            return false;
        }
        if (message.chat() == null) {
            return false;
        }
        return message.from() != null;
    }

    public abstract String executeCommand(Message message);
}
