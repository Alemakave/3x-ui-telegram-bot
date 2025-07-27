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
import ru.alemakave.xuitelegrambot.model.Client;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.service.ThreeXClient;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import java.util.Optional;

import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientRole.ADMIN;

@TGInlineButtonAnnotation
public class SettingsConnectionButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;
    public ThreeXClient threeXClient;

    public SettingsConnectionButton(ClientedTelegramBot telegramBot) {
        super(telegramBot, "Настройки", "/settingsClient");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage maybeInaccessibleMessage = callbackQuery.maybeInaccessibleMessage();
        long chatId = maybeInaccessibleMessage.chat().id();
        Object[] args = getCallbackArgs(update);

        if (canAccess(ADMIN) && args.length > 0) {
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

            long connectionId = Long.parseLong(getCallbackArgs(update)[0]);
            Connection connection = threeXConnection.get(connectionId);

            Optional<Client> client = connection.getSettings().getClients()
                    .stream()
                    .filter(c -> !c.getTgId().isEmpty())
                    .findAny();

            if (client.isPresent()) {
                ChangeRoleInlineButton changeRoleInlineButton = new ChangeRoleInlineButton(telegramBot);
                changeRoleInlineButton.addCallbackArg(connectionId);
                changeRoleInlineButton.addCallbackArg(client.get().getId());
                changeRoleInlineButton.setButtonText("Изменить роль");
                keyboardMarkup.addRow(changeRoleInlineButton.getButton());
            }

            ToggleConnectionEnableInlineButton toggleConnectionEnableInlineButton = new ToggleConnectionEnableInlineButton(telegramBot);
            toggleConnectionEnableInlineButton.addCallbackArg(connectionId);
            toggleConnectionEnableInlineButton.setButtonText(connection.isEnable() ? "Выключить" : "Включить");
            keyboardMarkup.addRow(toggleConnectionEnableInlineButton.getButton());

            DeleteConnectionInlineButton deleteButton = new DeleteConnectionInlineButton(telegramBot);
            deleteButton.addCallbackArg(connectionId);
            keyboardMarkup.addRow(deleteButton.getButton());

            GetConnectionInlineButton backButton = new GetConnectionInlineButton(telegramBot);
            backButton.setButtonText("Назад");
            backButton.addCallbackArg(connectionId);
            keyboardMarkup.addRow(backButton.getButton());

            if (((Message) maybeInaccessibleMessage).photo() == null) {
                EditMessageText message = new EditMessageText(chatId, maybeInaccessibleMessage.messageId(), "Настройки подключения");
                message.replyMarkup(keyboardMarkup);
                telegramBot.execute(message);
            } else {
                SendMessage message = new SendMessage(chatId, "Настройки подключения");
                message.replyMarkup(keyboardMarkup);
                telegramBot.execute(message);

                DeleteMessage deleteMessage = new DeleteMessage(chatId, maybeInaccessibleMessage.messageId());
                telegramBot.execute(deleteMessage);
            }
        }
    }

    @Override
    public TelegramClient.TelegramClientRole getAccessLevel() {
        return ADMIN;
    }
}
