package ru.alemakave.xuitelegrambot.commands.telegram;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendDocument;
import ru.alemakave.xuitelegrambot.annotations.TGCommandAnnotation;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.service.ThreeXWeb;

import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientRole.ADMIN;

@TGCommandAnnotation
public class CreateBackupCommand extends TGCommand {
    public ThreeXWeb threeXWeb;

    public CreateBackupCommand(ClientedTelegramBot telegramBot) {
        super(telegramBot);
    }

    @Override
    public String getCommand() {
        return "/create_backup";
    }

    @Override
    public void action(Update update) {
        long chatId = update.message().chat().id();

        SendDocument sendDocument = new SendDocument(chatId, threeXWeb.exportBackup());
        sendDocument.fileName("x-ui.db");
        telegramBot.execute(sendDocument);
    }

    @Override
    public TelegramClient.TelegramClientRole getAccessLevel() {
        return ADMIN;
    }
}
