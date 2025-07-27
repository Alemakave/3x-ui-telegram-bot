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
import ru.alemakave.xuitelegrambot.service.ThreeXClient;
import ru.alemakave.xuitelegrambot.utils.UuidValidator;

import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientRole.ADMIN;
import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientRole.USER;

@TGInlineButtonAnnotation
public class ChangeRoleInlineButton extends TGInlineButton {
    public ThreeXClient threeXClient;

    public ChangeRoleInlineButton(ClientedTelegramBot telegramBot) {
        super(telegramBot, "", "/changeRole");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage maybeInaccessibleMessage = callbackQuery.maybeInaccessibleMessage();
        long chatId = maybeInaccessibleMessage.chat().id();
        Object[] args = getCallbackArgs(update);

        String msg;

        if (args.length > 1) {
            long connectionId = Long.parseLong(args[0].toString());
            String clientId = args[1].toString();

            if (!UuidValidator.isValidUUID(clientId)) {
                InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

                SettingsConnectionButton settingsConnectionButton = new SettingsConnectionButton(telegramBot);
                settingsConnectionButton.addCallbackArg(connectionId);
                keyboardMarkup.addRow(settingsConnectionButton.getButton());

                SendMessage sendMessage = new SendMessage(chatId, "Некорректный UUID клиента");
                sendMessage.replyMarkup(keyboardMarkup);

                return;
            }

            Client client = threeXClient.getClientByUUID(clientId).getClient();

            String[] tgIdParts = client.getTgId().split(":");
            String role = tgIdParts[0];
            String tgId = tgIdParts[1];

            if (role.equalsIgnoreCase("ADMIN")) {
                role = USER.toString();
                msg = String.format("\"%s\" теперь пользователь.", client.getEmail());
            } else if (role.equalsIgnoreCase("USER")) {
                role = ADMIN.toString();
                msg = String.format("\"%s\" теперь администратор.", client.getEmail());
            } else {
                sendMessage(maybeInaccessibleMessage, connectionId, chatId, "Не удалось изменить роль.");
                return;
            }

            client.setTgId(role + ":" + tgId);

            threeXClient.updateClient(clientId, client);

            sendMessage(maybeInaccessibleMessage, connectionId, chatId, msg);
        }
    }

    private void sendMessage(MaybeInaccessibleMessage maybeInaccessibleMessage, long connectionId, long chatId, String msg) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        SettingsConnectionButton settingsConnectionButton = new SettingsConnectionButton(telegramBot);
        settingsConnectionButton.addCallbackArg(connectionId);
        settingsConnectionButton.setButtonText("Назад");

        keyboardMarkup.addRow(settingsConnectionButton.getButton());

        if (maybeInaccessibleMessage == null) {
            SendMessage message = new SendMessage(chatId, "");
            message.replyMarkup(keyboardMarkup);
            telegramBot.execute(message);
        } else {
            if (((Message)maybeInaccessibleMessage).photo() == null) {
                EditMessageText message = new EditMessageText(chatId, maybeInaccessibleMessage.messageId(), msg);
                message.replyMarkup(keyboardMarkup);
                telegramBot.execute(message);
            } else {
                SendMessage message = new SendMessage(chatId, msg);
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
