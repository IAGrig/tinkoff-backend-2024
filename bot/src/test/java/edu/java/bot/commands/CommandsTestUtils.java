package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import org.mockito.Mockito;

public class CommandsTestUtils {
    public static Message getMockMessage(String text) {
        Message mockMessage = Mockito.mock(Message.class);
        User mockUser = Mockito.mock(User.class);
        Chat mockChat = Mockito.mock(Chat.class);
        Mockito.when(mockMessage.text()).thenReturn(text);
        Mockito.when(mockUser.firstName()).thenReturn("TESTER");
        Mockito.when(mockMessage.from()).thenReturn(mockUser);
        Mockito.when(mockMessage.chat()).thenReturn(mockChat);

        return mockMessage;
    }
}
