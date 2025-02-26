package ru.alemakave.xuitelegrambot.commands.telegram;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.annotations.TGCommandAnnotation;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

@TGCommandAnnotation
public class LogoutCommand extends TGCommand {
    public ThreeXConnection threeXConnection;

    public LogoutCommand(ClientedTelegramBot telegramBot) {
        super(telegramBot);
    }

    @Override
    public String getCommand() {
        return "/logout";
    }

    @Override
    public void action(Update update) {
        long chatId = update.message().chat().id();

        if (telegramBot.isAuthorizedClient(chatId)) {
            telegramBot.unauthUserById(chatId, telegramClient -> {
                Connection connection = threeXConnection.get(telegramClient.getConnectionId());
                connection.getSettings().getClients()
                        .stream()
                            .filter(client -> client.getId().equals(telegramClient.getClientUuid()))
                            .toList()
                        .get(0)
                        .setTgId("");
                threeXConnection.update(telegramClient.getConnectionId(), connection);

                SendMessage message = new SendMessage(chatId, "Бот отключен. Для повторного входа введите конфигурацию пользователя или код");
                message.replyMarkup(new ReplyKeyboardRemove());
                telegramBot.execute(message);

                return null;
            });
        } else {
            SendMessage message = new SendMessage(chatId, "Вы не авторизованы!");
            message.replyMarkup(new ReplyKeyboardRemove());
            telegramBot.execute(message);
        }
    }

    @Override
    public TelegramClient.TelegramClientRole getAccessLevel() {
        return TelegramClient.TelegramClientRole.USER;
    }
}
