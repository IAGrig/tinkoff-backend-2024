package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.commands.stub.CommandHandler;
import edu.java.bot.bot.commands.stub.StartCommandHandler;
import edu.java.bot.bot.links.LinksHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static edu.java.bot.commands.CommandsTestUtils.getMockMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class StartCommandHandlerTest {
    private final String expectedStartAnswer = "Welcome, TESTER. Try /help command to discover all functionality.";
    private Database mockDatabase;
    private LinksHandler mockLinksHandler;
    private Message mockMessage;

    @BeforeEach
    public void setUpMocks() {
        mockDatabase = Mockito.mock(Database.class);
        mockLinksHandler = Mockito.mock(LinksHandler.class);
        mockMessage = Mockito.mock(Message.class);
    }

    @Test
    @DisplayName("/start correct test")
    public void startCorrectTest() {
        mockMessage = getMockMessage("/start");
        CommandHandler handler = new StartCommandHandler("/start", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo(expectedStartAnswer);
    }

    @Test
    @DisplayName("/start incorrect message test")
    public void startIncorrectTest() {
        CommandHandler handler = new StartCommandHandler("/start", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("I can't handle it.");
    }

    @Test
    @DisplayName("without /start test")
    public void withoutCommandTest() {
        mockMessage = getMockMessage("test");
        CommandHandler handler = new StartCommandHandler("/start", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("It is not command. Try to use /help command.");
    }
}
