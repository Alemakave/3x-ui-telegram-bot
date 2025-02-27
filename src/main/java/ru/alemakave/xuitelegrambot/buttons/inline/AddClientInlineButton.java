package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.alemakave.xuitelegrambot.actions.GetConnectionAction;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.service.ThreeXClient;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientRole.ADMIN;

@TGInlineButtonAnnotation
@EqualsAndHashCode(callSuper = true)
@ToString
public class AddClientInlineButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;
    public ThreeXClient threeXClient;

    public AddClientInlineButton(ClientedTelegramBot telegramBot) {
        super(telegramBot, "Добавить клиента", "/add_client");
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

        int inboundId = Integer.parseInt(receivedMessageParts[1]);

        threeXClient.addClient(inboundId);

        GetConnectionAction.action(telegramBot, threeXConnection, threeXClient, chatId, inboundId, maybeInaccessibleMessage);
    }

    @Override
    public TelegramClient.TelegramClientRole getAccessLevel() {
        return ADMIN;
    }
}
