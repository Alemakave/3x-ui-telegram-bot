package ru.alemakave.xuitelegrambot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class Client {
    private String id;
    private Flow flow;
    private String email;
    private int limitIp;
    private int totalGB;
    private long expiryTime;
    private boolean enable;
    private String tgId;
    private String subId;
    private byte reset;
    private String comment;
}
