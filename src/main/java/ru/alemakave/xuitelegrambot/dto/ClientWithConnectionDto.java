package ru.alemakave.xuitelegrambot.dto;

import lombok.*;
import ru.alemakave.xuitelegrambot.model.Client;
import ru.alemakave.xuitelegrambot.model.Connection;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ClientWithConnectionDto {
    private Connection connection;
    private Client client;
}
