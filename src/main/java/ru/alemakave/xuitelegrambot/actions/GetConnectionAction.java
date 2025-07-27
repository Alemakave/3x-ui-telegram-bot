package ru.alemakave.xuitelegrambot.actions;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import ru.alemakave.xuitelegrambot.buttons.inline.*;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.model.*;
import ru.alemakave.xuitelegrambot.service.ThreeXClient;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;
import ru.alemakave.xuitelegrambot.utils.FileUtils;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.List;

import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientRole.USER;

@Slf4j
public class GetConnectionAction {
    public static void action(ClientedTelegramBot telegramBot, ThreeXConnection threeXConnection, ThreeXClient threeXClient, long chatId, long connectionId, MaybeInaccessibleMessage maybeInaccessibleMessage) {
        Connection connection = threeXConnection.get(connectionId);
        if (connection == null) {
            SendMessage sendMessage = new SendMessage(chatId, "Подключение с ID=" + connectionId + " не найдено!");
            telegramBot.execute(sendMessage);
            return;
        }

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        ConnectionSettings connectionSettings = connection.getSettings();
        if (connectionSettings != null) {
            List<Client> clients = connectionSettings.getClients();
            for (Client client : clients) {
                GenerateClientConnectionQRInlineButton generateClientConnectionQR = new GenerateClientConnectionQRInlineButton(telegramBot);
                generateClientConnectionQR.addCallbackArgs(connection.getId(), client.getId());
                generateClientConnectionQR.setButtonText("Получить QR код " + client.getEmail());

                DeleteClientInlineButton deleteClient = new DeleteClientInlineButton(telegramBot);
                if (deleteClient.canAccess(telegramBot.getClientByChatId(chatId))) {
                    deleteClient.addCallbackArgs(connection.getId(), client.getId());
                    deleteClient.setButtonText(deleteClient.getButtonText() + " " + client.getEmail());

                    keyboardMarkup.addRow(generateClientConnectionQR.getButton(), deleteClient.getButton());
                } else {
                    keyboardMarkup.addRow(generateClientConnectionQR.getButton());
                }
            }
        }

        if (telegramBot.getClientByChatId(chatId).getRole() == USER) {
            GetConnectionInlineButton updateButton = new GetConnectionInlineButton(telegramBot);
            updateButton.addCallbackArg(connection.getId());
            updateButton.setButtonText("Обновить");
            keyboardMarkup.addRow(updateButton.getButton());
        }

        AddClientInlineButton addClientButton = new AddClientInlineButton(telegramBot);
        if (addClientButton.canAccess(telegramBot.getClientByChatId(chatId))) {
            addClientButton.addCallbackArg(connection.getId());
            keyboardMarkup.addRow(addClientButton.getButton());

            SettingsConnectionButton settingsConnectionButton = new SettingsConnectionButton(telegramBot);
            settingsConnectionButton.addCallbackArg(connection.getId());
            keyboardMarkup.addRow(settingsConnectionButton.getButton());

            ListConnectionsInlineButton backButton = new ListConnectionsInlineButton(telegramBot);
            backButton.setButtonText("Назад");
            keyboardMarkup.addRow(backButton.getButton());
        }

        String info = generateMinimizedConnectionInfo(connection, threeXClient, threeXConnection);

        if (maybeInaccessibleMessage == null) {
            SendMessage message = new SendMessage(chatId, info);
            message.replyMarkup(keyboardMarkup);
            telegramBot.execute(message);
        } else {
            if (((Message)maybeInaccessibleMessage).photo() == null) {
                EditMessageText message = new EditMessageText(chatId, maybeInaccessibleMessage.messageId(), info);
                message.replyMarkup(keyboardMarkup);
                telegramBot.execute(message);
            } else {
                SendMessage message = new SendMessage(chatId, info);
                message.replyMarkup(keyboardMarkup);
                telegramBot.execute(message);

                DeleteMessage deleteMessage = new DeleteMessage(chatId, maybeInaccessibleMessage.messageId());
                telegramBot.execute(deleteMessage);
            }
        }
    }

    private static String generateMinimizedConnectionInfo(Connection connection, ThreeXClient threeXClient, ThreeXConnection threeXConnection) {
        List<String> emailsOnline = threeXConnection.onlines();
        StringBuilder msg = new StringBuilder();
        msg.append(connection.getRemark()).append("\n");
        msg.append("\uD83D\uDCA1 Активен: ").append(connection.isEnable() ? " Да✅" : " Нет❌").append("\n");
        long expiryTime = connection.getExpiryTime();
        String expiryTimeStr;
        if (expiryTime == 0) {
            expiryTimeStr = "♾ Неограниченно";
        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(expiryTime);
            expiryTimeStr = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(calendar.getTime());
        }
        msg.append("\uD83D\uDCC5 Дата окончания: ").append(expiryTimeStr).append("\n");
        msg.append("\uD83D\uDC65 Клиенты:").append("\n");
        for (Client client : connection.getSettings().getClients()) {
            msg.append("   \uD83D\uDCE7 Email: ").append(client.getEmail()).append("\n");
            msg.append("   \uD83C\uDF10 Статус: ").append(emailsOnline.contains(client.getEmail()) ? "Онлайн \uD83D\uDFE2" : "Оффлайн \uD83D\uDD34").append("\n");
            ClientTraffics traffics = threeXClient.getClientTrafficsByEmail(client.getEmail());
            msg.append("   \uD83D\uDD3C Исходящий трафик: ↑").append(FileUtils.byteToDisplaySize(traffics.getUp())).append("\n");
            msg.append("   \uD83D\uDD3D Входящий трафик: ↓").append(FileUtils.byteToDisplaySize(traffics.getDown())).append("\n\n");
        }

        try {
            msg.append("\uD83D\uDD04 Обновлено: ").append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(GregorianCalendar.from(ZonedDateTime.now()).getTime()));
        } catch (IllegalArgumentException e) {
            log.error("Ошибка вывода даты и времени обновления: " + e.getMessage());
            msg.append("\uD83D\uDD04 Обновлено: ОШИБКА");
        }

        return msg.toString();
    }
}
