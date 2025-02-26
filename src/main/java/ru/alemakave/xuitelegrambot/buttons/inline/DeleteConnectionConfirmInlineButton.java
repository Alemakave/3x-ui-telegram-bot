package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.model.messages.DeleteConnectionMessage;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientRole.ADMIN;

@TGInlineButtonAnnotation
public class DeleteConnectionConfirmInlineButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;

    public DeleteConnectionConfirmInlineButton(ClientedTelegramBot telegramBot) {
        super(telegramBot, "Да. Удалить.", "/delete_connection_confirm");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage message = callbackQuery.maybeInaccessibleMessage();
        long chatId = message.chat().id();
        String[] args = getCallbackArgs(update);

        if (args.length < 1) {
            SendMessage sendMessage = new SendMessage(chatId, "Не удалось обнаружить ID подключения!");
            telegramBot.execute(sendMessage);
            return;
        }

        try {
            int connectionId = Integer.parseInt(args[0]);
            DeleteConnectionMessage deleteMessage = (DeleteConnectionMessage)threeXConnection.delete(connectionId);

            String messageText = String.format("Подключение с ID=%s %s", connectionId, (deleteMessage.isSuccess() ? "удалено." : "не удалось удалить."));

            EditMessageText editMessage = new EditMessageText(chatId, message.messageId(), messageText);
            ListConnectionsInlineButton backButton = new ListConnectionsInlineButton(telegramBot);
            backButton.setButtonText("Назад");
            editMessage.replyMarkup(new InlineKeyboardMarkup(backButton.getButton()));

            telegramBot.execute(editMessage);
        } catch (NumberFormatException e) {
            SendMessage sendMessage = new SendMessage(chatId, "Не удалось преобразовать ID подключения в число!");
            telegramBot.execute(sendMessage);
        }
    }

    @Override
    public TelegramClient.TelegramClientRole getAccessLevel() {
        return ADMIN;
    }
}
