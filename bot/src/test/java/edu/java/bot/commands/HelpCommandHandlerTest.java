package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.commands.stub.CommandHandler;
import edu.java.bot.bot.commands.stub.HelpCommandHandler;
import edu.java.bot.bot.commands.stub.StartCommandHandler;
import edu.java.bot.bot.links.LinksHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static edu.java.bot.commands.CommandsTestUtils.getMockMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HelpCommandHandlerTest {
    private final String expectedHelpAnswer = """
        Welcome to Link Updates Notifier. \n
        Here is list of all available commands:
        /start - register in this bot.
        /help - show this message.
        /list - show list of tracking links.
        /track <link> - start to track <link>.
        /untrack <id> - end tracking of link with id=<id>.
        """;
    private Database mockDatabase;
    private LinksHandler mockLinksHandler;

    @BeforeEach
    public void setUpMocks() {
        mockDatabase = Mockito.mock(Database.class);
        mockLinksHandler = Mockito.mock(LinksHandler.class);
    }

    @Test
    @DisplayName("/help correct test")
    public void helpCorrectTest() {
        Message mockMessage = getMockMessage("/help");
        CommandHandler handler = new HelpCommandHandler("/help", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo(expectedHelpAnswer);
    }

    @Test
    @DisplayName("/help incorrect message test")
    public void helpIncorrectTest() {
        Message mockMessage = Mockito.mock(Message.class);
        CommandHandler handler = new HelpCommandHandler("/help", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("I can't handle it.");
    }

    @Test
    @DisplayName("without /help test")
    public void withoutCommandTest() {
        Message mockMessage = getMockMessage("test");
        CommandHandler handler = new StartCommandHandler("/help", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("It is not command. Try to use /help command.");
    }
}
