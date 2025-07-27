package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

@TGInlineButtonAnnotation
public class ToggleConnectionEnableInlineButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;

    public ToggleConnectionEnableInlineButton(ClientedTelegramBot telegramBot) {
        super(telegramBot, "Включить/Выключить клиента", "/toggleClientEnabled");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage maybeInaccessibleMessage = callbackQuery.maybeInaccessibleMessage();
        long chatId = maybeInaccessibleMessage.chat().id();
        Object[] args = getCallbackArgs(update);

        if (args.length > 0) {
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            long connectionId = Long.parseLong(getCallbackArgs(update)[0]);

            Connection connection = threeXConnection.get(connectionId);

            connection.setEnable(!connection.isEnable());
            threeXConnection.update(connectionId, connection);

            SettingsConnectionButton backButton = new SettingsConnectionButton(telegramBot);
            backButton.addCallbackArg(connectionId);
            backButton.setButtonText("Назад");
            keyboardMarkup.addRow(backButton.getButton());

            if (((Message) maybeInaccessibleMessage).photo() == null) {
                EditMessageText message = new EditMessageText(chatId, maybeInaccessibleMessage.messageId(), "Подключение " + (connection.isEnable() ? "включено" : "выключено"));
                message.replyMarkup(keyboardMarkup);
                telegramBot.execute(message);
            } else {
                SendMessage message = new SendMessage(chatId, "Подключение " + (connection.isEnable() ? "включено" : "выключено"));
                message.replyMarkup(keyboardMarkup);
                telegramBot.execute(message);

                DeleteMessage deleteMessage = new DeleteMessage(chatId, maybeInaccessibleMessage.messageId());
                telegramBot.execute(deleteMessage);
            }
        }
    }

    @Override
    public TelegramClient.TelegramClientRole getAccessLevel() {
        return TelegramClient.TelegramClientRole.ADMIN;
    }
}
