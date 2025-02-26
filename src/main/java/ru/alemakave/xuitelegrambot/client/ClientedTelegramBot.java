package ru.alemakave.xuitelegrambot.client;

import com.pengrad.telegrambot.TelegramBot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ClientedTelegramBot extends TelegramBot {
    private final Map<Long, TelegramClient> authorizedClients = new HashMap<>();

    public ClientedTelegramBot(String botToken) {
        super(botToken);
    }

    public boolean isAuthorizedClient(Long chatId) {
        return authorizedClients.containsKey(chatId);
    }

    public TelegramClient getClientByChatId(Long chatId) {
        return authorizedClients.get(chatId);
    }

    public void authUser(TelegramClient client) {
        authUser(client, null);
    }

    public void authUser(TelegramClient client, Function<TelegramClient, Void> successFunction) {
        authorizedClients.put(client.getTgChatId(), client);

        if (successFunction != null) {
            successFunction.apply(client);
        }
    }

    public void unauthUserById(long chatId, Function<TelegramClient, Void> successFunction) {
        TelegramClient removableClient = authorizedClients.get(chatId);
        TelegramClient client = new TelegramClient(removableClient.getTgChatId(), removableClient.getRole(), removableClient.getConnectionId(), removableClient.getClientUuid());
        authorizedClients.remove(chatId);

        if (successFunction != null) {
            successFunction.apply(client);
        }
    }
}
