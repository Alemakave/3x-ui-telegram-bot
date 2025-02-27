package ru.alemakave.xuitelegrambot.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ConnectionSettings {
    private ArrayList<Client> clients;
    private String decryption;
    private Object fallbacks;
}
