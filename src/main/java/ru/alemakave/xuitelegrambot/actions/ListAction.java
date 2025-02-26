package ru.alemakave.xuitelegrambot.actions;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.buttons.inline.AddConnectionInlineButton;
import ru.alemakave.xuitelegrambot.buttons.inline.GetConnectionInlineButton;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import java.util.List;

public class ListAction {
    public static void action(ClientedTelegramBot telegramBot, ThreeXConnection threeXConnection, long chatId, int messageId) {
        StringBuilder message = new StringBuilder("Список подключений:\n\n");

        List<Connection> connections = threeXConnection.list();
        InlineKeyboardButton[][] buttons = new InlineKeyboardButton[(int)Math.ceil((double)connections.size() / 2) + 1][2];

        int j = 0;
        for (int i = 0; i < connections.size(); i++) {
            if (i > 0 && i % 2 == 0) {
                j++;
            }

            Connection connection = connections.get(i);

            if (connection.isEnable()) {
                message.append("✅ ");
            } else {
                message.append("☑ ");
            }
            message.append("Имя: ");
            message.append(connection.getRemark());
            message.append("\n\n");

            GetConnectionInlineButton getConnectionInlineButton = new GetConnectionInlineButton(telegramBot);
            getConnectionInlineButton.setButtonText(connection.getRemark());
            getConnectionInlineButton.addCallbackArg(connection.getId());

            buttons[j][i % 2] = getConnectionInlineButton.getButton();
        }
        if (buttons[buttons.length - 2][1] == null) {
            buttons[buttons.length - 2] = new InlineKeyboardButton[]{buttons[buttons.length - 2][0]};
        }
        buttons[buttons.length - 1] = new InlineKeyboardButton[]{new AddConnectionInlineButton(telegramBot).getButton()};

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(buttons);

        if (messageId == -1) {
            SendMessage sendMessage = new SendMessage(chatId, message.toString());
            sendMessage.replyMarkup(keyboardMarkup);
            telegramBot.execute(sendMessage);
        } else {
            EditMessageText editMessage = new EditMessageText(chatId, messageId, message.toString());
            editMessage.replyMarkup(keyboardMarkup);
            telegramBot.execute(editMessage);
        }
    }
}
