package ru.alemakave.xuitelegrambot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
public class Connection {
    private long id;
    private long up;
    private long down;
    private long total;
    private String remark;
    private boolean enable;
    private long expiryTime;
    private Object clientStats;
    private String listen;
    private int port;
    private String protocol;
    private ConnectionSettings settings;
    private ConnectionStreamSettings streamSettings;
    private String tag;
    private ConnectionSniffing sniffing;
    private ConnectionAllocate allocate;
}
