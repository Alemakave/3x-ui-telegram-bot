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
import ru.alemakave.xuitelegrambot.model.Client;
import ru.alemakave.xuitelegrambot.service.ThreeXClient;
import ru.alemakave.xuitelegrambot.utils.UuidValidator;

import java.util.Random;

@TGInlineButtonAnnotation
public class DeleteClientInlineButton extends TGInlineButton {
    public ThreeXClient threeXClient;

    // Допустимое значение от 1 до Integer.MAX_VALUE
    public static final int DELETE_CLIENT_BUTTONS_COUNT = 4;

    public DeleteClientInlineButton(ClientedTelegramBot telegramBot) {
        super(telegramBot, "Удалить", "/delete_client");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage message = callbackQuery.maybeInaccessibleMessage();
        long chatId = message.chat().id();
        String[] args = getCallbackArgs(update);

        long connectionId;

        try {
            connectionId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            SendMessage sendMessage = new SendMessage(chatId, "Не удалось преобразовать ID подключения в число!");
            telegramBot.execute(sendMessage);
            return;
        }

        String uuid = args[1];
        if (!UuidValidator.isValidUUID(uuid)) {
            SendMessage sendMessage = new SendMessage(chatId, "Неверно указан UUID пользователя.");
            telegramBot.execute(sendMessage);
        }

        Client client = threeXClient.getClientByUUID(uuid).getClient();

        String messageText = String.format("Вы уверены, что хотите удалить клиента \"%s\"?", client.getEmail());

        EditMessageText editMessage = new EditMessageText(chatId, message.messageId(), messageText);

        InlineKeyboardButton[] inlineKeyboardButtons = new InlineKeyboardButton[DELETE_CLIENT_BUTTONS_COUNT];
        for (int i = 0; i < DELETE_CLIENT_BUTTONS_COUNT; i++) {
            GetConnectionInlineButton backButton = new GetConnectionInlineButton(telegramBot);
            backButton.addCallbackArg(connectionId);
            backButton.setButtonText("Нет");
            inlineKeyboardButtons[i] = backButton.getButton();
        }
        DeleteClientConfirmInlineButton confirmInlineButton = new DeleteClientConfirmInlineButton(telegramBot);
        confirmInlineButton.addCallbackArgs(connectionId, uuid);
        inlineKeyboardButtons[new Random().nextInt(DELETE_CLIENT_BUTTONS_COUNT)] = confirmInlineButton.getButton();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        for (int i = 0; i < DELETE_CLIENT_BUTTONS_COUNT; i++) {
            inlineKeyboardMarkup.addRow(inlineKeyboardButtons[i]);
        }

        editMessage.replyMarkup(inlineKeyboardMarkup);

        telegramBot.execute(editMessage);
    }

    @Override
    public TelegramClient.TelegramClientRole getAccessLevel() {
        return TelegramClient.TelegramClientRole.ADMIN;
    }
}
