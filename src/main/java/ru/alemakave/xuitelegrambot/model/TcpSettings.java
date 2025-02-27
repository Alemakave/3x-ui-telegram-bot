package ru.alemakave.xuitelegrambot.model;

import lombok.Data;

@Data
public class TcpSettings {
    private boolean acceptProxyProtocol;
    private Header header;

    @Data
    public static class Header {
        private String type;
    }
}
