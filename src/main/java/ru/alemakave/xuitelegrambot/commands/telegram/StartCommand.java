package ru.alemakave.xuitelegrambot.commands.telegram;

import com.pengrad.telegrambot.model.Update;
import ru.alemakave.xuitelegrambot.actions.StartAction;
import ru.alemakave.xuitelegrambot.annotations.TGCommandAnnotation;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.service.ThreeXClient;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

@TGCommandAnnotation
public class StartCommand extends TGCommand {
    public ThreeXConnection threeXConnection;
    public ThreeXClient threeXClient;

    public StartCommand(ClientedTelegramBot telegramBot) {
        super(telegramBot);
    }

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public void action(Update update) {
        long chatId = update.message().chat().id();

        StartAction.action(telegramBot, chatId, threeXConnection, threeXClient);
    }

    @Override
    public TelegramClient.TelegramClientRole getAccessLevel() {
        return TelegramClient.TelegramClientRole.USER;
    }
}
