package ru.alemakave.xuitelegrambot.model;

import lombok.Data;

@Data
public class ClientTraffics {
    private long id;
    private long inboundId;
    private boolean enable;
    private String email;
    private long up;
    private long down;
    private long expiryTime;
    private long total;
    private int reset;
}
