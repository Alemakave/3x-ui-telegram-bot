package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.actions.GetConnectionAction;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.service.ThreeXClient;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientRole.USER;

@TGInlineButtonAnnotation
public class GetConnectionInlineButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;
    public ThreeXClient threeXClient;

    public GetConnectionInlineButton(ClientedTelegramBot telegramBot) {
        super(telegramBot, "", "/get");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage maybeInaccessibleMessage = callbackQuery.maybeInaccessibleMessage();
        long chatId = maybeInaccessibleMessage.chat().id();
        String receivedMessage = callbackQuery.data();

        String[] receivedMessageParts = receivedMessage.split(" ");

        if (receivedMessageParts.length < 2) {
            SendMessage sendMessage = new SendMessage(chatId, "При запросе не был указан ID подключения!");
            telegramBot.execute(sendMessage);
            return;
        }

        try {
            long connectionId = Long.parseLong(receivedMessageParts[1]);

            GetConnectionAction.action(telegramBot, threeXConnection, threeXClient, chatId, connectionId, maybeInaccessibleMessage);
        } catch (NumberFormatException e) {
            SendMessage sendMessage = new SendMessage(chatId, String.format("Не удалось преобразовать ID (\"%s\") в число", receivedMessageParts[1]));
            telegramBot.execute(sendMessage);
        }
    }

    @Override
    public TelegramClient.TelegramClientRole getAccessLevel() {
        return USER;
    }
}
