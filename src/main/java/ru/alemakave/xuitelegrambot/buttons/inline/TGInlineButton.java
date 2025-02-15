package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import lombok.*;

import static ru.alemakave.xuitelegrambot.utils.CommandUtils.getArguments;

@EqualsAndHashCode
@ToString
public abstract class TGInlineButton {
    protected final TelegramBot telegramBot;
    @Getter
    @Setter
    private String buttonText;
    @Getter
    private String callbackData;

    public TGInlineButton(TelegramBot telegramBot, String buttonText, String callbackData) {
        this.telegramBot = telegramBot;
        this.buttonText = buttonText;
        this.callbackData = callbackData;
    }

    public InlineKeyboardButton getButton() {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

        inlineKeyboardButton.setCallbackData(callbackData);
        inlineKeyboardButton.setText(buttonText);

        return inlineKeyboardButton;
    }

    public void addCallbackArg(Object argument) {
        callbackData += " " + argument.toString();
    }

    public void addCallbackArgs(Object... arguments) {
        for (Object argument : arguments) {
            addCallbackArg(argument);
        }
    }

    public String[] getCallbackArgs(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        String receivedMessage = callbackQuery.data();
        return getArguments(receivedMessage);
    }

    public abstract void action(Update update);
}
