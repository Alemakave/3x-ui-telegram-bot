package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import ru.alemakave.xuitelegrambot.actions.ListAction;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientRole.ADMIN;

@TGInlineButtonAnnotation
public class ListConnectionsInlineButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;

    public ListConnectionsInlineButton(ClientedTelegramBot telegramBot) {
        super(telegramBot, "Список подключений", "/list");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage maybeInaccessibleMessage = callbackQuery.maybeInaccessibleMessage();
        long chatId = maybeInaccessibleMessage.chat().id();

        ListAction.action(telegramBot, threeXConnection, chatId, maybeInaccessibleMessage.messageId());
    }

    @Override
    public TelegramClient.TelegramClientRole getAccessLevel() {
        return ADMIN;
    }
}
