package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.commands.stub.CommandHandler;
import edu.java.bot.bot.commands.stub.TrackCommandHandler;
import edu.java.bot.bot.links.LinksHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static edu.java.bot.commands.CommandsTestUtils.getMockMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TrackCommandHandlerTest {

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
    @DisplayName("/track correct test")
    public void correctTrackTest() {
        Mockito.when(mockLinksHandler.validateURLAndGetDomain("http://test.com/tests?id=1&user=tester"))
            .thenReturn("test");
        mockMessage = getMockMessage("/track http://test.com/tests?id=1&user=tester");
        CommandHandler handler = new TrackCommandHandler("/track", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        Mockito.verify(mockLinksHandler).validateURLAndGetDomain("http://test.com/tests?id=1&user=tester");
        Mockito.verify(mockDatabase).createLink("test", "http://test.com/tests?id=1&user=tester");
        Mockito.verify(mockDatabase).addLinkToUser(0L, 0L);
        assertThat(actual).isEqualTo("Link added to your track list.");
    }

    @Test
    @DisplayName("/track disallowed domain test")
    public void disallowedDomainTest() {
        Mockito.when(mockLinksHandler.validateURLAndGetDomain("http://test.com/tests?id=1&user=tester"))
            .thenThrow(new IllegalArgumentException());
        mockMessage = getMockMessage("/track http://test.com/tests?id=1&user=tester");
        CommandHandler handler = new TrackCommandHandler("/track", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("Unsupported URL domain.");
    }

    @Test
    @DisplayName("/track no link test")
    public void noLinkTest() {
        mockMessage = getMockMessage("/track");
        CommandHandler handler = new TrackCommandHandler("/track", mockDatabase, mockLinksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("Please, enter your link URL in one line with /track command after whitespace.");
    }
}
