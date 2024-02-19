package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.database.Database;
import edu.java.bot.bot.commands.CommandHandler;
import edu.java.bot.bot.commands.UntrackCommandHandler;
import edu.java.bot.bot.links.LinksHandler;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import static edu.java.bot.commands.CommandsTestUtils.getMockMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UntrackCommandHandlerTest {

    @Test
    @DisplayName("/untrack correct test")
    public void correctUntrackTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler linksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("/untrack 0");
        CommandHandler handler = new UntrackCommandHandler("/untrack", mockDatabase, linksHandler);

        String actual = handler.handleCommand(mockMessage);

        Mockito.verify(mockDatabase).removeLinkFromUser(0L, 0L);
        assertThat(actual).isEqualTo("You stopped tracking link with id=0.");
    }

    @Test
    @DisplayName("/untrack not number id test")
    public void notNumberIDTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler linksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("/untrack http://test.com/tests?id=1&user=tester");
        CommandHandler handler = new UntrackCommandHandler("/untrack", mockDatabase, linksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("Link ID must be a number.");
    }

    @Test
    @DisplayName("/untrack no id test")
    public void noIDTest() {
        Database mockDatabase = Mockito.mock(Database.class);
        LinksHandler linksHandler = Mockito.mock(LinksHandler.class);
        Message mockMessage = getMockMessage("/untrack");
        CommandHandler handler = new UntrackCommandHandler("/untrack", mockDatabase, linksHandler);

        String actual = handler.handleCommand(mockMessage);

        assertThat(actual).isEqualTo("Please, enter link ID in one message with /untrack command after whitespace.");
    }
}
