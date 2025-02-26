package ru.alemakave.xuitelegrambot.listener;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.alemakave.xuitelegrambot.actions.StartAction;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.buttons.inline.ListConnectionsInlineButton;
import ru.alemakave.xuitelegrambot.buttons.inline.TGInlineButton;
import ru.alemakave.xuitelegrambot.client.ClientedTelegramBot;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.commands.telegram.TGCommand;
import ru.alemakave.xuitelegrambot.annotations.TGCommandAnnotation;
import ru.alemakave.xuitelegrambot.configuration.TelegramBotConfiguration;
import ru.alemakave.xuitelegrambot.dto.ClientWithConnectionDto;
import ru.alemakave.xuitelegrambot.model.Client;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.service.ThreeXClient;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;
import ru.alemakave.xuitelegrambot.utils.UuidValidator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientRole.*;
import static ru.alemakave.xuitelegrambot.client.TelegramClient.TelegramClientMode.*;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TelegramBotListener implements UpdatesListener {
    @Autowired
    private ClientedTelegramBot telegramBot;
    @Autowired
    private ThreeXConnection threeXConnection;
    @Autowired
    private ThreeXClient threeXClient;
    @Autowired
    private TelegramBotConfiguration telegramBotConfiguration;

    private final Map<String, TGCommand> commands = new HashMap<>();
    private final Map<String, TGInlineButton> buttons = new HashMap<>();

    @Override
    public int process(List<Update> listUpdates) {
        listUpdates.forEach(update -> {
            if (update.message() != null) {
                processMessage(update);
            } else if (update.callbackQuery() != null) {
                processCallbackQuery(update);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processMessage(Update update) {
        long chatId = update.message().chat().id();
        String receivedMessage = update.message().text();

        if (receivedMessage != null) {
            if (!telegramBot.isAuthorizedClient(chatId)) {
                if (receivedMessage.startsWith("vless://")) {
                    receivedMessage = receivedMessage.substring("vless://".length(), receivedMessage.indexOf("@"));
                }
                if (UuidValidator.isValidUUID(receivedMessage)) {
                    TelegramClient.TelegramClientRole role = TelegramClient.TelegramClientRole.USER;

                    if (telegramBotConfiguration.hasAdminUUID() && receivedMessage.equals(telegramBotConfiguration.getAdminUUID())) {
                        role = TelegramClient.TelegramClientRole.ADMIN;
                    }

                    if (authProcessMessage(chatId, role, receivedMessage)) {
                        SendMessage message = new SendMessage(chatId, "Бот подключен");
                        telegramBot.execute(message);

                        StartAction.action(telegramBot, chatId, threeXConnection, threeXClient);
                    } else {
                        SendMessage message = new SendMessage(chatId, "Пользователь не найден");
                        telegramBot.execute(message);
                    }
                } else {
                    if (receivedMessage.equals("/start") && commands.containsKey("/start")) {
                        commands.get("/start").action(update);
                        return;
                    }

                    if (commands.containsKey(receivedMessage)) {
                        SendMessage message = new SendMessage(chatId, "Вы не авторизованны! Введите код пользователя или конфигурацию");
                        telegramBot.execute(message);
                    } else {
                        SendMessage message = new SendMessage(chatId, "Введен некорректный код пользователя! Проверьте корректность введенных данных и повторите попытку");
                        telegramBot.execute(message);
                    }
                }
            } else {
                if (telegramBot.getClientByChatId(chatId).getMode() == ENTER_CONNECTION_NAME
                        && telegramBot.getClientByChatId(chatId).getRole() == ADMIN) {
                    createConnectionProcessMessage(chatId, receivedMessage);
                    return;
                }

                boolean hasInCache = commands.containsKey(receivedMessage.split(" ")[0]);
                if (!hasInCache) {
                    SendMessage sendMessage = new SendMessage(chatId, String.format("Команда \"%s\" не распознана", receivedMessage));
                    telegramBot.execute(sendMessage);
                    return;
                }

                if (commands.get(receivedMessage).canAccess(telegramBot.getClientByChatId(chatId))) {
                    commands.get(receivedMessage.split(" ")[0]).action(update);
                } else {
                    SendMessage message = new SendMessage(chatId, "Недостаточно прав!");
                    telegramBot.execute(message);
                }
            }
        }
    }

    private void processCallbackQuery(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        String callbackText = callbackQuery.data();
        long chatId = callbackQuery.maybeInaccessibleMessage().chat().id();

        if (!telegramBot.isAuthorizedClient(chatId)) {
            EditMessageText message = new EditMessageText(chatId, callbackQuery.maybeInaccessibleMessage().messageId(), "Вы не авторизованы!");
            telegramBot.execute(message);

            AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackQuery.id());
            telegramBot.execute(answer);

            return;
        }

        boolean hasInCache = buttons.containsKey(callbackText.split(" ")[0]);
        if (hasInCache) {
            TGInlineButton button = buttons.get(callbackText.split(" ")[0]);

            if (button.canAccess(telegramBot.getClientByChatId(chatId))) {
                button.action(update);
            } else {
                EditMessageText message = new EditMessageText(chatId, callbackQuery.maybeInaccessibleMessage().messageId(), "Недостаточно прав!");
                telegramBot.execute(message);

                AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackQuery.id());
                telegramBot.execute(answer);
            }
        } else {
            SendMessage sendMessage = new SendMessage(update.callbackQuery().data(), String.format("Callback \"%s\" не найден", callbackText));
            telegramBot.execute(sendMessage);
        }

        AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackQuery.id());
        telegramBot.execute(answer);
    }

    private void registerCommands() {
        Reflections reflections = new Reflections("");
        Map<String, Object> thisClassFieldsByType = getThisClassDeclaredFields();

        reflections.getTypesAnnotatedWith(TGCommandAnnotation.class).forEach(clazz -> {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Registering command class: {}", clazz.getSimpleName());
                    log.debug("    Class fields: ");
                    Arrays.stream(clazz.getFields()).forEach(field -> log.debug("        {}", field));
                    log.debug("    Class declared fields: ");
                    Arrays.stream(clazz.getDeclaredFields()).forEach(field -> log.debug("        {}", field));
                }

                Constructor<?> constructor = clazz.getDeclaredConstructor(ClientedTelegramBot.class);
                List<Field> declaredFields = Arrays.stream(clazz.getDeclaredFields()).toList();

                TGCommand command = (TGCommand) constructor.newInstance(telegramBot);

                if (!declaredFields.isEmpty()) {
                    declaredFields.forEach(field -> {
                        if (thisClassFieldsByType.containsKey(field.getType().getTypeName())) {
                            try {
                                log.debug("\tSign field {} => {}", thisClassFieldsByType.get(field.getType().getTypeName()), field.getName());
                                field.set(command, thisClassFieldsByType.get(field.getType().getTypeName()));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }

                commands.put(command.getCommand(), command);
                log.info("Registered command: {}", command.getCommand());
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void registerButtons() {
        Reflections reflections = new Reflections("");
        Map<String, Object> thisClassFieldsByType = getThisClassDeclaredFields();

        reflections.getTypesAnnotatedWith(TGInlineButtonAnnotation.class).stream()
                .sorted(Comparator.comparing(Class::getName))
                .forEach(clazz -> {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Registering button class: {}", clazz.getSimpleName());
                    log.debug("    Class fields: ");
                    Arrays.stream(clazz.getFields()).forEach(field -> log.debug("        {}", field));
                    log.debug("    Class declared fields: ");
                    Arrays.stream(clazz.getDeclaredFields()).forEach(field -> log.debug("        {}", field));
                }

                Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
                Object[] constructorParametersValue;

                Parameter[] parameters = constructor.getParameters();
                constructorParametersValue = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];

                    List<?> buttonsInParameter = buttons.values().stream()
                            .filter(tgInlineButton -> tgInlineButton.getClass().equals(parameter.getType()))
                            .toList();

                    if (!buttonsInParameter.isEmpty()) {
                        constructorParametersValue[i] = buttonsInParameter.get(0);
                    } else if (parameter.getType().equals(ClientedTelegramBot.class)) {
                        constructorParametersValue[i] = telegramBot;
                    } else {
                        String message = constructor.getName() + ".<init>" +
                                Arrays.stream(parameters)
                                        .map(c -> c == null ? "null" : c.toString())
                                        .collect(Collectors.joining(", ", "(", ")")) + " for param " + parameter;
                        throw new NoSuchMethodException(message);
                    }
                }

                List<Field> declaredFields = Arrays.stream(clazz.getDeclaredFields()).toList();

                TGInlineButton button = (TGInlineButton) constructor.newInstance(constructorParametersValue);

                if (!declaredFields.isEmpty()) {
                    declaredFields.forEach(field -> {
                        if (thisClassFieldsByType.containsKey(field.getType().getTypeName())) {
                            try {
                                log.debug("\tSign field {} => {}", thisClassFieldsByType.get(field.getType().getTypeName()), field.getName());
                                field.set(button, thisClassFieldsByType.get(field.getType().getTypeName()));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (getClass().getTypeName().equals(field.getType().getTypeName())) {
                            try {
                                log.debug("\tSign field {} => {}", this, field.getName());
                                field.set(button, this);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }

                buttons.put(button.getCallbackData(), button);
                log.info("Registered button: {}", button.getCallbackData());
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                if (e.getMessage().contains("<init>")) {
                    log.error("Не удалось найти конструктор");
                    log.error("Доступные конструкторы:");
                    for (Constructor<?> declaredConstructor : clazz.getDeclaredConstructors()) {
                        log.error("    {}", declaredConstructor.toString());
                    }
                }
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * @return Map<String, Object>, где:
     *    Ключ - путь к классу типа переменной.
     *    Значение - значение переменной.
     */
    private Map<String, Object> getThisClassDeclaredFields() {
        return Arrays.stream(TelegramBotListener.this.getClass().getDeclaredFields())
                        .filter(field -> field.getType().getTypeName().startsWith("ru"))
                        .collect(
                                Collectors.toMap(
                                        field1 -> field1.getType().getTypeName(),
                                        field2 -> {
                                            try {
                                                return field2.get(TelegramBotListener.this);
                                            } catch (IllegalAccessException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                )
                        );
    }

    private boolean authProcessMessage(long chatId, TelegramClient.TelegramClientRole role, String uuid) {
        AtomicBoolean success = new AtomicBoolean(false);

        ClientWithConnectionDto clientWithConnection = threeXClient.getClientByUUID(uuid);

        telegramBot.authUser(new TelegramClient(chatId, role, clientWithConnection.getConnection().getId(), uuid), telegramClient -> {
            Client client = clientWithConnection.getClient();
            client.setTgId(role + ":" + chatId);
            Connection connection = clientWithConnection.getConnection();
            threeXConnection.update(connection.getId(), connection);

            success.set(true);
            return null;
        });

        return success.get();
    }

    private void createConnectionProcessMessage(long chatId, String newConnectionName) {
        telegramBot.getClientByChatId(chatId).setMode(TelegramClient.TelegramClientMode.NONE);
        threeXConnection.add(newConnectionName);

        SendMessage sendMessage = new SendMessage(chatId, "Подключение создано");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(new ListConnectionsInlineButton(telegramBot).getButton());
        sendMessage.replyMarkup(keyboardMarkup);
        telegramBot.execute(sendMessage);
    }

    @PostConstruct
    private void postConstruct() {
        registerCommands();
        registerButtons();
        telegramBot.setUpdatesListener(this);
        log.info("TelegramBotListener initialized");
    }
}
