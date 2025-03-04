package ru.alemakave.xuitelegrambot.model;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
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
