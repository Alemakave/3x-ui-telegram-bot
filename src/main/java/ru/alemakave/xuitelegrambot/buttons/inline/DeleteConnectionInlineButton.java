package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import java.util.Random;

import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientRole.ADMIN;

@TGInlineButtonAnnotation
public class DeleteConnectionInlineButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;

    // Допустимое значение от 1 до Integer.MAX_VALUE
    public static final int DELETE_CONNECTION_BUTTONS_COUNT = 4;

    public DeleteConnectionInlineButton(ClientedTelegramBot telegramBot) {
        super(telegramBot, "Удалить подключение", "/delete_connection");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage message = callbackQuery.maybeInaccessibleMessage();
        long chatId = message.chat().id();
        String receivedMessage = callbackQuery.data();
        String[] receivedMessageParts = receivedMessage.split(" ");
        String[] args = getCallbackArgs(update);

        if (receivedMessageParts.length < 2 || args.length == 0) {
            SendMessage sendMessage = new SendMessage(chatId, "Не удалось обнаружить ID подключения!");
            telegramBot.execute(sendMessage);
            return;
        }

        try {
            int connectionId = Integer.parseInt(args[0]);
            Connection connection = threeXConnection.get(connectionId);

            String messageText = String.format("Вы уверены, что хотите удалить подключение \"%s\"?", connection.getRemark());

            EditMessageText editMessage = new EditMessageText(chatId, message.messageId(), messageText);

            InlineKeyboardButton[] inlineKeyboardButtons = new InlineKeyboardButton[DELETE_CONNECTION_BUTTONS_COUNT];
            for (int i = 0; i < DELETE_CONNECTION_BUTTONS_COUNT; i++) {
                GetConnectionInlineButton backButton = new GetConnectionInlineButton(telegramBot);
                backButton.addCallbackArg(connectionId);
                backButton.setButtonText("Нет");
                inlineKeyboardButtons[i] = backButton.getButton();
            }
            DeleteConnectionConfirmInlineButton confirmInlineButton = new DeleteConnectionConfirmInlineButton(telegramBot);
            confirmInlineButton.addCallbackArg(connectionId);
            inlineKeyboardButtons[new Random().nextInt(DELETE_CONNECTION_BUTTONS_COUNT)] = confirmInlineButton.getButton();

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            for (int i = 0; i < DELETE_CONNECTION_BUTTONS_COUNT; i++) {
                inlineKeyboardMarkup.addRow(inlineKeyboardButtons[i]);
            }

            editMessage.replyMarkup(inlineKeyboardMarkup);

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
