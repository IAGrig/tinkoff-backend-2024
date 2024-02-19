package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.commands.CommandHandler;
import edu.java.bot.bot.commands.StartCommandHandler;
import edu.java.bot.bot.links.LinksHandler;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import static edu.java.bot.commands.CommandsTestUtils.getMockMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class StartCommandHandlerTest {
    private final String expectedStartAnswer = "Welcome, TESTER. Try /help command to discover all functionality.";

    @Test
    @DisplayName("/start correct test")
    public void startCorrectTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("/start");
        CommandHandler handler = new StartCommandHandler("/start", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo(expectedStartAnswer);
    }

    @Test
    @DisplayName("/start incorrect message test")
    public void startIncorrectTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = Mockito.mock(Message.class);
        CommandHandler handler = new StartCommandHandler("/start", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("I can't handle it.");
    }

    @Test
    @DisplayName("without /start test")
    public void withoutCommandTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("test");
        CommandHandler handler = new StartCommandHandler("/start", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("It is not command. Try to use /help command.");
    }
}
