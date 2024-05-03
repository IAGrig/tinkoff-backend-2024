package edu.java.bot.bot.commands.springHttp;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.bot.links.LinksHandler;
import edu.java.bot.httpClients.ScrapperHttpClient;
import edu.java.dto.LinkResponse;
import edu.java.exceptions.ApiException;

public class TrackCommandHandler extends CommandHandler {

    public TrackCommandHandler(String command, ScrapperHttpClient scrapperClient, LinksHandler linksHandler) {
        super(command, scrapperClient, linksHandler);
    }

    @Override
    public String executeCommand(Message message) {
        try {
            String linkUrl = message.text().split(" ")[1].toLowerCase();
            String domain = linksHandler.validateURLAndGetDomain(linkUrl);
            LinkResponse response = scrapperClient.trackLink(message.from().id(), linkUrl);
            return "Link added to your track list.";
        } catch (IndexOutOfBoundsException e) {
            return "Please, enter your link URL in one line with /track command after whitespace.";
        } catch (IllegalArgumentException e) {
            return "Unsupported URL domain.";
        } catch (ApiException e) {
            return e.getMessage();
        }
    }
}
