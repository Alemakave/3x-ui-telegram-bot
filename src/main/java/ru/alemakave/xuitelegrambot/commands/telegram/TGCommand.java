package ru.alemakave.xuitelegrambot.commands.telegram;

import com.pengrad.telegrambot.model.Update;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;

public abstract class TGCommand {
    protected final ClientedTelegramBot telegramBot;

    public TGCommand(ClientedTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public boolean canAccess(TelegramClient telegramClient) {
        return this.getAccessLevel().compareTo(telegramClient.getRole()) >= 0;
    }

    public abstract String getCommand();
    public abstract void action(Update update);
    public abstract TelegramClient.TelegramClientRole getAccessLevel();
}
