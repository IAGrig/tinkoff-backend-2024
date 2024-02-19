package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.commands.CommandHandler;
import edu.java.bot.bot.commands.HelpCommandHandler;
import edu.java.bot.bot.commands.StartCommandHandler;
import edu.java.bot.bot.links.LinksHandler;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import static edu.java.bot.commands.CommandsTestUtils.getMockMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HelpCommandHandlerTest {
    private final String expectedHelpAnswer = """
        Welcome to Link Updates Notifier. \n
        Here is list of all available commands:
        /help - show this message.
        /list - show list of tracking links.
        /track <link> - start to track <link>.
        /untrack <id> - end tracking of link with id=<id>.
        """;

    @Test
    @DisplayName("/help correct test")
    public void helpCorrectTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("/help");
        CommandHandler handler = new HelpCommandHandler("/help", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo(expectedHelpAnswer);
    }

    @Test
    @DisplayName("/help incorrect message test")
    public void helpIncorrectTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = Mockito.mock(Message.class);
        CommandHandler handler = new HelpCommandHandler("/help", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("I can't handle it.");
    }

    @Test
    @DisplayName("without /help test")
    public void withoutCommandTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("test");
        CommandHandler handler = new StartCommandHandler("/help", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("It is not command. Try to use /help command.");
    }
}
