package ru.alemakave.xuitelegrambot.model;

import lombok.Data;

@Data
public class Client {
    /**
     * UUID клиента
     */
    private String id;
    /**
     * Flow
     */
    private Flow flow;
    /**
     * Email клиента
     */
    private String email;
    /**
     * Лимит подключенных ip-адресов к одному клиенту
     */
    private int limitIp;
    /**
     * Лимит трафика в Гб
     */
    private int totalGB;
    /**
     * Дата-время окончания действия
     */
    private long expiryTime;
    /**
     * Активность клиент
     */
    private boolean enable;
    /**
     * Привязанный идентификатор чата в телеграме
     */
    private String tgId;
    /**
     * Подайди клиента
     */
    private String subId;
    /**
     * Режим сброса клиента
     */
    private byte reset;
    /**
     * Комментарий к клиенту
     */
    private String comment;
}
