package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.commands.CommandsManager;
import edu.java.bot.bot.links.LinksHandler;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import static edu.java.bot.commands.CommandsTestUtils.getMockMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CommandsManagerTest {
    @Test
    @DisplayName("CommandsManager correct test")
    public void commandManagerCorrectTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("/start");
        CommandsManager manager = new CommandsManager(mockDatabase, mockLinksHandler);

        String actual = manager.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("Welcome, TESTER. Try /help command to discover all functionality.");
    }

    @Test
    @DisplayName("CommandsManager unknown command test")
    public void unknownCommandTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("test");
        CommandsManager manager = new CommandsManager(mockDatabase, mockLinksHandler);

        String actual = manager.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("It is not command. Try to use /help command.");
    }
}
