package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.commands.CommandHandler;
import edu.java.bot.bot.commands.TrackCommandHandler;
import edu.java.bot.bot.links.LinksHandler;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import static edu.java.bot.commands.CommandsTestUtils.getMockMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TrackCommandHandlerTest {

    @Test
    @DisplayName("/track correct test")
    public void correctTrackTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler linksHandler = Mockito.mock(LinksHandler.class);
        Mockito.when(linksHandler.validateURLAndGetDomain("http://test.com/tests?id=1&user=tester"))
            .thenReturn("test");
        Message mockMessage = getMockMessage("/track http://test.com/tests?id=1&user=tester");
        CommandHandler handler = new TrackCommandHandler("/track", mockDatabase, linksHandler);

        String actual = handler.handleCommand(mockMessage);

        Mockito.verify(linksHandler).validateURLAndGetDomain("http://test.com/tests?id=1&user=tester");
        Mockito.verify(mockDatabase).createLink("test", "http://test.com/tests?id=1&user=tester");
        Mockito.verify(mockDatabase).addLinkToUser(0L, 0L);
        assertThat(actual).isEqualTo("Link added to your track list.");
    }

    @Test
    @DisplayName("/track disallowed domain test")
    public void disallowedDomainTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler linksHandler = Mockito.mock(LinksHandler.class);
        Mockito.when(linksHandler.validateURLAndGetDomain("http://test.com/tests?id=1&user=tester"))
            .thenThrow(new IllegalArgumentException());
        Message mockMessage = getMockMessage("/track http://test.com/tests?id=1&user=tester");
        CommandHandler handler = new TrackCommandHandler("/track", mockDatabase, linksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("Unsupported URL domain.");
    }

    @Test
    @DisplayName("/track no link test")
    public void noLinkTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler linksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("/track");
        CommandHandler handler = new TrackCommandHandler("/track", mockDatabase, linksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("Please, enter your link URL in one line with /track command after whitespace.");
    }
}
