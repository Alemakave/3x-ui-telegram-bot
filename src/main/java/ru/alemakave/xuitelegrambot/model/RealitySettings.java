package ru.alemakave.xuitelegrambot.model;

import lombok.Data;

import java.util.List;

@Data
public class RealitySettings {
    private boolean show;
    private int xver;
    private String dest;
    private List<String> serverNames;
    private String privateKey;
    private String minClient;
    private String maxClient;
    private int maxTimediff;
    private List<String> shortIds;
    private RealityConnectionSettings settings;
}
