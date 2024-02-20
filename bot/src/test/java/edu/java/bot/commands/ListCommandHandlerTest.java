package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.database.entities.Link;
import edu.java.bot.bot.commands.CommandHandler;
import edu.java.bot.bot.commands.ListCommandHandler;
import edu.java.bot.bot.commands.StartCommandHandler;
import edu.java.bot.bot.links.LinksHandler;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import static edu.java.bot.commands.CommandsTestUtils.getMockMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ListCommandHandlerTest {
    private final String expectedEmptyListAnswer = "You don't track any links.";
    private final String expectedSingleElementAnswer = """
        Here are all your links:
        [test]:
            (id: 1) https://test.test
        """;
    private final String expectedDoubleElementAnswer = """
        Here are all your links:
        [test]:
            (id: 1) https://test.test
        [testing]:
            (id: 2) https://testing.test
        """;

    @Test
    @DisplayName("/list correct empty list test")
    public void emptyListCorrectTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("/list");
        CommandHandler handler = new ListCommandHandler("/list", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo(expectedEmptyListAnswer);
    }

    @Test
    @DisplayName("/list correct single element list test")
    public void singleElementListCorrectTest() {
        Database mockDatabase = getSingleElementMockDatabase(0L);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("/list");
        CommandHandler handler = new ListCommandHandler("/list", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo(expectedSingleElementAnswer);
    }

    @Test
    @DisplayName("/list correct multiple element list test")
    public void multipleElementsListCorrectTest() {
        Database mockDatabase = getMultipleElementMockDatabase(0L);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("/list");
        CommandHandler handler = new ListCommandHandler("/list", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo(expectedDoubleElementAnswer);
    }

    @Test
    @DisplayName("/list incorrect message test")
    public void listIncorrectTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = Mockito.mock(Message.class);
        CommandHandler handler = new ListCommandHandler("/list", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("I can't handle it.");
    }

    @Test
    @DisplayName("without /list test")
    public void withoutCommandTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler mockLinksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("test");
        CommandHandler handler = new StartCommandHandler("/list", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("It is not command. Try to use /help command.");
    }

    private Database getSingleElementMockDatabase(Long userID) {
        Database database = Mockito.mock(Database.class);
        Link link = new Link(1L, "test", "https://test.test");
        Mockito.when(database.getAllUserLinks(userID)).thenReturn(List.of(link));
        return database;
    }

    private Database getMultipleElementMockDatabase(Long userID) {
        Database database = Mockito.mock(Database.class);
        Link link1 = new Link(1L, "test", "https://test.test");
        Link link2 = new Link(2L, "testing", "https://testing.test");
        Mockito.when(database.getAllUserLinks(userID)).thenReturn(List.of(link1, link2));
        return database;
    }
}
