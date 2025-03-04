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
import ru.alemakave.xuitelegrambot.service.ThreeXClient;
import ru.alemakave.xuitelegrambot.utils.UuidValidator;

@TGInlineButtonAnnotation
public class DeleteClientConfirmInlineButton extends TGInlineButton {
    public ThreeXClient threeXClient;

    public DeleteClientConfirmInlineButton(ClientedTelegramBot telegramBot) {
        super(telegramBot, "Да. Удалить.", "/delete_client_confirm");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage message = callbackQuery.maybeInaccessibleMessage();
        long chatId = message.chat().id();
        String[] receivedMessageArguments = getCallbackArgs(update);

        if (receivedMessageArguments.length != 2) {
            SendMessage sendMessage = new SendMessage(chatId, "Неверное количество аргументов для удаления клиента. Необходимо передать ID подключения и UUID пользователя.");
            telegramBot.execute(sendMessage);

            return;
        }

        long inboundId = -1;

        try {
            inboundId = Long.parseLong(receivedMessageArguments[0]);
        } catch (NumberFormatException e) {
            SendMessage sendMessage = new SendMessage(chatId, "Не удалось преобразовать ID подключения в число!");
            telegramBot.execute(sendMessage);
        }
        String uuid = receivedMessageArguments[1];

        if (!UuidValidator.isValidUUID(uuid)) {
            SendMessage sendMessage = new SendMessage(chatId, "Неверно указан UUID пользователя.");
            telegramBot.execute(sendMessage);

            return;
        }

        String clientEmail = threeXClient.getClientByUUID(uuid).getClient().getEmail();

        boolean isSuccess = threeXClient.deleteClientByClientId(inboundId, uuid);

        String messageText = String.format("Клиент \"%s\" %s.", clientEmail, (isSuccess ? "удален." : "не был удален."));

        EditMessageText editMessage = new EditMessageText(chatId, message.messageId(), messageText);
        GetConnectionInlineButton backButton = new GetConnectionInlineButton(telegramBot);
        backButton.addCallbackArg(inboundId);
        backButton.setButtonText("Назад");
        editMessage.replyMarkup(new InlineKeyboardMarkup(backButton.getButton()));

        telegramBot.execute(editMessage);
    }

    @Override
    public TelegramClient.TelegramClientRole getAccessLevel() {
        return TelegramClient.TelegramClientRole.ADMIN;
    }
}
