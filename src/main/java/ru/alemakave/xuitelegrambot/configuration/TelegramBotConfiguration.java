package ru.alemakave.xuitelegrambot.configuration;

import com.pengrad.telegrambot.request.DeleteMyCommands;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.exception.UnsetException;
import ru.alemakave.xuitelegrambot.model.Client;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.model.ConnectionSettings;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import java.util.ArrayList;
import java.util.List;

@Configuration
@PropertySource(value = {"file:./application.yml"}, ignoreResourceNotFound = true)
public class TelegramBotConfiguration {
    @Value("${telegram.bot.token:}")
    private String token;
    @Getter
    @Value("${telegram.bot.owner.uuid:}")
    private String adminUUID;

    @Autowired
    private ThreeXConnection threeXConnection;

    @Bean
    public ClientedTelegramBot telegramBot() {
        if (token == null || token.isEmpty()) {
            throw new UnsetException("TelegramBot token unset!");
        }

        ClientedTelegramBot bot = new ClientedTelegramBot(token);
        bot.execute(new DeleteMyCommands());
        getClientsFromServer().forEach(bot::authUser);
        return bot;
    }

    private List<TelegramClient> getClientsFromServer() {
        List<TelegramClient> telegramClients = new ArrayList<>();

        List<Connection> connections = threeXConnection.list();

        connections.forEach(connection -> {
            ConnectionSettings connectionSettings = connection.getSettings();
            if (connectionSettings == null) {
                return;
            }

            List<Client> clients = connectionSettings.getClients();
            if (clients == null || clients.isEmpty()) {
                return;
            }

            clients.forEach(client -> {
                String tgIdAndRole = client.getTgId();

                if (tgIdAndRole == null || tgIdAndRole.isEmpty()) {
                    return;
                }

                if (tgIdAndRole.startsWith("Admin:")) {
                    long chatId = Long.parseLong(tgIdAndRole.substring("Admin:".length()));

                    TelegramClient telegramClient = new TelegramClient(chatId, TelegramClient.TelegramClientRole.ADMIN, connection.getId(), client.getId());
                    telegramClients.add(telegramClient);
                } else if (tgIdAndRole.startsWith("User:")) {
                    long chatId = Long.parseLong(tgIdAndRole.substring("User:".length()));

                    TelegramClient telegramClient = new TelegramClient(chatId, TelegramClient.TelegramClientRole.USER, connection.getId(), client.getId());
                    telegramClients.add(telegramClient);
                }
            });
        });

        return telegramClients;
    }

    public boolean hasAdminUUID() {
        return adminUUID != null && !adminUUID.isEmpty();
    }
}
