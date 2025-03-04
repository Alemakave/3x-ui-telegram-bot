package ru.alemakave.xuitelegrambot.model;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Certificate {
    private String privateKey;
    private String publicKey;
}
