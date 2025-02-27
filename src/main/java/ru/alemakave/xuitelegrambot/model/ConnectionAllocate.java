package ru.alemakave.xuitelegrambot.model;

import lombok.Data;

@Data
public class ConnectionAllocate {
    private String strategy;
    private int refresh;
    private int concurrency;
}
