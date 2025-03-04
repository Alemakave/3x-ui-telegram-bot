package ru.alemakave.xuitelegrambot.model;

import lombok.Data;

import java.util.List;

@Data
public class ConnectionStreamSettings {
    private String network;
    private String security;
    private List<Object> externalProxy;
    private RealitySettings realitySettings;
    private TcpSettings tcpSettings;
}
