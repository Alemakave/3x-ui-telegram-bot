package ru.alemakave.xuitelegrambot.model;

import lombok.Data;

import java.util.List;

@Data
public class ConnectionSniffing {
    private boolean enabled;
    private List<String> destOverride;
    private boolean metadataOnly;
    private boolean routeOnly;
}
