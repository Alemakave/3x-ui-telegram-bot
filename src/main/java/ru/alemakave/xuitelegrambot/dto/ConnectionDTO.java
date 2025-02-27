package ru.alemakave.xuitelegrambot.dto;

import lombok.Data;
import ru.alemakave.xuitelegrambot.model.ConnectionClientStat;

import java.util.List;

@Data
public class ConnectionDTO {
    private long id;
    private long up;
    private long down;
    private long total;
    private String remark;
    private boolean enable;
    private long expiryTime;
    private List<ConnectionClientStat> clientStats;
    private String listen;
    private int port;
    private String protocol;
    private String settings;
    private String streamSettings;
    private String tag;
    private String sniffing;
    private String allocate;
}
