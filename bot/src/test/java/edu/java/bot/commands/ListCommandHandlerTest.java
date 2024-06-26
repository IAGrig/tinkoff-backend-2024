package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.database.entities.Link;
import edu.java.bot.bot.commands.stub.CommandHandler;
import edu.java.bot.bot.commands.stub.ListCommandHandler;
import edu.java.bot.bot.commands.stub.StartCommandHandler;
import edu.java.bot.bot.links.LinksHandler;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    @DisplayName("/list correct empty list test")
    public void emptyListCorrectTest() {
        Message mockMessage = getMockMessage("/list");
        CommandHandler handler = new ListCommandHandler("/list", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo(expectedEmptyListAnswer);
    }

    @Test
    @DisplayName("/list correct single element list test")
    public void singleElementListCorrectTest() {
        Database mockDatabase = getSingleElementMockDatabase(0L);
        mockMessage = getMockMessage("/list");
        CommandHandler handler = new ListCommandHandler("/list", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo(expectedSingleElementAnswer);
    }

    @Test
    @DisplayName("/list correct multiple element list test")
    public void multipleElementsListCorrectTest() {
        Database mockDatabase = getMultipleElementMockDatabase(0L);
        mockMessage = getMockMessage("/list");
        CommandHandler handler = new ListCommandHandler("/list", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo(expectedDoubleElementAnswer);
    }

    @Test
    @DisplayName("/list incorrect message test")
    public void listIncorrectTest() {
        CommandHandler handler = new ListCommandHandler("/list", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("I can't handle it.");
    }

    @Test
    @DisplayName("without /list test")
    public void withoutCommandTest() {
        mockMessage = getMockMessage("test");
        CommandHandler handler = new StartCommandHandler("/list", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("It is not command. Try to use /help command.");
    }

    private Database getSingleElementMockDatabase(Long userID) {
        Link link = new Link(1L, "test", "https://test.test", null, null, null);
        Mockito.when(mockDatabase.getAllUserLinks(userID)).thenReturn(List.of(link));
        return mockDatabase;
    }

    private Database getMultipleElementMockDatabase(Long userID) {
        Link link1 = new Link(1L, "test", "https://test.test", null, null, null);
        Link link2 = new Link(2L, "testing", "https://testing.test", null, null, null);
        Mockito.when(mockDatabase.getAllUserLinks(userID)).thenReturn(List.of(link1, link2));
        return mockDatabase;
    }
}
