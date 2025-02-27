package ru.alemakave.xuitelegrambot.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class ConnectionClientStat {
    private long id;
    private long inboundId;
    private boolean enable;
    private String email;
    private long up;
    private long down;
    private long expiryTile;
    private long total;
    private int reset;
}
