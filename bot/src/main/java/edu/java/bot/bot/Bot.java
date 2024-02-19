package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import edu.database.Database;
import edu.database.StubDatabase;
import edu.java.bot.bot.commands.CommandsManager;
import edu.java.bot.bot.links.LinksHandler;
import java.util.HashMap;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

// may be make BotManager class that should be CommandLineRunner and start bot
@Component
@Log4j2
public class Bot implements CommandLineRunner {

    private final Environment env;
    private final LinksHandler linksHandler;
    private final Database database;
    private final CommandsManager commandsManager;

    @Autowired
    public Bot(Environment env, LinksHandler linksHandler) {
        this.env = env;
        this.linksHandler = linksHandler;
        this.database = new StubDatabase(new HashMap<>(), new HashMap<>());
        this.commandsManager = new CommandsManager(database, linksHandler);
    }

    @Override
    public void run(String... args) throws Exception {
        TelegramBot bot = new TelegramBot(env.getProperty("telegram_api_key"));
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                log.info(update.toString());
                Long chatId = update.message().chat().id();
                String answer = commandsManager.handleCommand(update.message());
                bot.execute(new SendMessage(chatId, answer));
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

        log.info("Bot started.");
    }
}
