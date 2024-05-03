package edu.java.bot.bot.commands.springHttp;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.bot.links.LinksHandler;
import edu.java.bot.httpClients.ScrapperHttpClient;
import edu.java.exceptions.ApiException;

public class StartCommandHandler extends CommandHandler {

    public StartCommandHandler(String command, ScrapperHttpClient scrapperClient, LinksHandler linksHandler) {
        super(command, scrapperClient, linksHandler);
    }

    @Override
    public String executeCommand(Message message) {
        Long userID = message.from().id();
        try {
            return scrapperClient.registerChat(userID);
        } catch (ApiException ex) {
            return ex.getMessage();
        }
    }
}
