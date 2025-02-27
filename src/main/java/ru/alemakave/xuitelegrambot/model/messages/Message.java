package ru.alemakave.xuitelegrambot.model.messages;

import lombok.Data;

@Data
public class Message<T> {
    private boolean success;
    private String msg;
    private T obj;
}
