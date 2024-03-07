package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.database.Database;
import edu.database.StubDatabase;
import edu.java.bot.bot.commands.CommandsManager;
import edu.java.bot.bot.links.LinksHandler;
import java.util.HashMap;
import java.util.HashSet;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

// may be make BotManager class that should be CommandLineRunner and start bot
@Component
@Log4j2
public class Bot implements CommandLineRunner {
    private final Database database;
    private final CommandsManager commandsManager;
    private final TelegramBot bot;

    @Autowired
    public Bot(Environment env, LinksHandler linksHandler) {
        this.database = new StubDatabase(new HashMap<>(), new HashMap<>(), new HashSet<>());
        this.commandsManager = new CommandsManager(database, linksHandler);
        this.bot = new TelegramBot(env.getProperty("telegram_api_key"));
    }

    @Override
    public void run(String... args) {
        setUpdatesListener();
        setMyCommands();

        log.info("Bot started.");
    }

    public void sendMessage(Long chatId, String message) {
        bot.execute(new SendMessage(chatId, message));
    }

    private void setUpdatesListener() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                log.info(update.toString());
                Long chatId = update.message().chat().id();
                String answer = commandsManager.handleCommand(update.message());
                bot.execute(new SendMessage(chatId, answer));
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void setMyCommands() {
        BotCommand startCommand = new BotCommand("/start", "register in this bot");
        BotCommand helpCommand = new BotCommand("/help", "show help message about all commands");
        BotCommand listCommand = new BotCommand("/list", "show the links you are tracking");
        BotCommand trackCommand = new BotCommand("/track", "start tracking the link");
        BotCommand untrackCommand = new BotCommand("/untrack", "stop tracking the link");
        SetMyCommands setCommands =
            new SetMyCommands(startCommand, helpCommand, listCommand, trackCommand, untrackCommand);
        bot.execute(setCommands);
    }
}
