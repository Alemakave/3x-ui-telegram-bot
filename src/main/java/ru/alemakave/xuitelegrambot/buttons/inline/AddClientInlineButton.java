package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.service.ThreeXClient;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

@TGInlineButtonAnnotation
@EqualsAndHashCode(callSuper = true)
@ToString
public class AddClientInlineButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;
    public ThreeXClient threeXClient;

    public AddClientInlineButton(TelegramBot telegramBot) {
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

        GetConnectionInlineButton backButton = new GetConnectionInlineButton(telegramBot);
        backButton.threeXConnection = threeXConnection;
        backButton.addCallbackArg(inboundId);
        backButton.action(update);
    }
}
