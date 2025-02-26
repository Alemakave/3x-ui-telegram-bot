package ru.alemakave.xuitelegrambot.actions;

import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.service.ThreeXClient;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

public class StartAction {
    public static void action(ClientedTelegramBot telegramBot, long chatId, ThreeXConnection threeXConnection, ThreeXClient threeXClient) {
        if (telegramBot.isAuthorizedClient(chatId)) {
            if (telegramBot.getClientByChatId(chatId).getRole() == TelegramClient.TelegramClientRole.ADMIN) {
                ListAction.action(telegramBot, threeXConnection, chatId, -1);
            } else {
                GetConnectionAction.action(telegramBot, threeXConnection, threeXClient, chatId, telegramBot.getClientByChatId(chatId).getConnectionId(), null);
            }
        } else {
            SendMessage message = new SendMessage(chatId, "Введите код пользователя или конфигурацию");
            telegramBot.execute(message);
        }
    }
}
