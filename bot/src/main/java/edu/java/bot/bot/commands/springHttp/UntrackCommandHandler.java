package edu.java.bot.bot.commands.springHttp;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.bot.links.LinksHandler;
import edu.java.bot.httpClients.ScrapperHttpClient;
import edu.java.dto.LinkResponse;
import edu.java.exceptions.ApiException;

public class UntrackCommandHandler extends CommandHandler {

    public UntrackCommandHandler(String command, ScrapperHttpClient scrapperClient, LinksHandler linksHandler) {
        super(command, scrapperClient, linksHandler);
    }

    @Override
    public String executeCommand(Message message) {
        Long userID = message.from().id();
        try {
            String linkUrl = message.text().split(" ")[1];
            LinkResponse response = scrapperClient.untrackLink(userID, linkUrl);
            return String.format("You stopped tracking link %s.", response.getUrl().toString());
        } catch (IndexOutOfBoundsException e) {
            return "Please, enter link URL in one message with /untrack command after whitespace.";
        } catch (ApiException e) {
            return e.getMessage();
        }
    }
}
