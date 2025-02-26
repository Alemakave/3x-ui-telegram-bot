package ru.alemakave.xuitelegrambot.dto;

import lombok.Data;
import ru.alemakave.xuitelegrambot.model.Client;
import ru.alemakave.xuitelegrambot.model.Connection;

@Data
public class ClientWithConnectionDto {
    private Connection connection;
    private Client client;
}
