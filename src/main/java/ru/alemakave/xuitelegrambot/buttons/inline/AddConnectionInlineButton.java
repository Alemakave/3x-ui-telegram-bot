package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientRole.ADMIN;

@TGInlineButtonAnnotation
public class AddConnectionInlineButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;

    public AddConnectionInlineButton(ClientedTelegramBot telegramBot) {
        super(telegramBot, "Добавить подключение", "/add_connection");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage maybeInaccessibleMessage = callbackQuery.maybeInaccessibleMessage();
        long chatId = maybeInaccessibleMessage.chat().id();

        telegramBot.getClientByChatId(chatId).setMode(TelegramClient.TelegramClientMode.ENTER_CONNECTION_NAME);

        DeleteMessage deleteMessage = new DeleteMessage(chatId, maybeInaccessibleMessage.messageId());
        telegramBot.execute(deleteMessage);

        SendMessage message = new SendMessage(chatId, "Введите название нового подключения:");
        telegramBot.execute(message);
    }

    @Override
    public TelegramClient.TelegramClientRole getAccessLevel() {
        return ADMIN;
    }
}
